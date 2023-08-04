package com.codezjx.andlinker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.codezjx.andlinker.adapter.DefaultCallAdapterFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.codezjx.andlinker.Utils.checkNotNull;

/**
 * AndLinker adapts a Java interface to IPC calls by using annotations on the declared methods to
 * define how requests are made. Create instances using {@linkplain Builder
 * the builder} and pass your interface to {@link #create} to generate an implementation.
 */
public final class AndLinker {
    
    private static final String TAG = "AndLinker";

    private final Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();
    private ServiceConnection mServiceConnection;
    private Invoker mInvoker;
    private Context mContext;
    private String mPackageName;
    private String mAction;
    private String mClassName;
    private List<CallAdapter.Factory> mAdapterFactories;
    private Dispatcher mDispatcher;
    private ITransfer mTransferService;
    private ICallback mCallback;
    private BindCallback mBindCallback;
    
    private AndLinker(Context context, String packageName, String action, String className, List<CallAdapter.Factory> adapterFactories) {
        mContext = context;
        mPackageName = packageName;
        mAction = action;
        mClassName = className;
        mInvoker = new Invoker();
        mAdapterFactories = adapterFactories;
        mDispatcher = new Dispatcher();
        mServiceConnection = createServiceConnection();
        mCallback = createCallback();
    }

    /**
     * Create an implementation defined by the remote service interface.
     */
    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    public <T> T create(final Class<T> service) {
        Utils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        ServiceMethod serviceMethod = loadServiceMethod(method);
                        RemoteCall remoteCall = new RemoteCall(mTransferService, serviceMethod, args, mDispatcher);
                        return serviceMethod.getCallAdapter().adapt(remoteCall);
                    }
                });
    }

    /**
     * Connect to the remote service.
     */
    public void bind() {
        if (isBind()) {
            Logger.d(TAG, "Already bind, just return.");
            return;
        }
        Intent intent = new Intent();
        if (!Utils.isStringBlank(mAction)) {
            intent.setAction(mAction);
        } else if (!Utils.isStringBlank(mClassName)) {
            intent.setClassName(mPackageName, mClassName);
        }
        // After android 5.0+, service Intent must be explicit.
        intent.setPackage(mPackageName);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Disconnect from the remote service.
     */
    public void unbind() {
        if (!isBind()) {
            Logger.d(TAG, "Already unbind, just return.");
            return;
        }
        mContext.unbindService(mServiceConnection);
        handleUnBind(true);
    }

    /**
     * Register client interface implementation called by remote service.
     */
    public void registerObject(Object target) {
        mInvoker.registerObject(target);
    }

    /**
     * Unregister client interface implementation.
     */
    public void unRegisterObject(Object target) {
        mInvoker.unRegisterObject(target);
    }

    /**
     * Set callback to be invoked when linker is bind or unBind.
     */
    public void setBindCallback(BindCallback bindCallback) {
        mBindCallback = bindCallback;
    }

    /**
     * Return the remote service bind state.
     */
    public boolean isBind() {
        return mTransferService != null;
    }

    CallAdapter<?, ?> findCallAdapter(Type returnType, Annotation[] annotations) {
        checkNotNull(returnType, "returnType == null");
        checkNotNull(annotations, "annotations == null");
        
        for (int i = 0, count = mAdapterFactories.size(); i < count; i++) {
            CallAdapter<?, ?> adapter = mAdapterFactories.get(i).get(returnType, annotations);
            if (adapter != null) {
                return adapter;
            }
        }

        return DefaultCallAdapterFactory.INSTANCE.get(returnType, annotations);
    }

    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Logger.d(TAG, "onServiceConnected:" + name + " service:" + service);
                mTransferService = ITransfer.Stub.asInterface(service);
                try {
                    mTransferService.register(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                fireOnBind();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Logger.d(TAG, "onServiceDisconnected:" + name);
                handleUnBind(false);
            }
        };
    }
    
    private void handleUnBind(boolean unRegister) {
        Logger.d(TAG, "handleUnBind:" + unRegister);
        if (mTransferService == null) {
            Logger.e(TAG, "Error occur, TransferService was null when service disconnected.");
            fireOnUnBind();
            return;
        }
        if (unRegister) {
            try {
                mTransferService.unRegister(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mTransferService = null;
        fireOnUnBind();
    }

    private void fireOnBind() {
        if (mBindCallback != null) {
            mBindCallback.onBind();
        }
    }

    private void fireOnUnBind() {
        if (mBindCallback != null) {
            mBindCallback.onUnBind();
        }
    }

    private ICallback createCallback() {
        return new ICallback.Stub() {
            @Override
            public Response callback(Request request) throws RemoteException {
                Logger.d(TAG, "Receive callback in client:" + request.toString());
                return mInvoker.invoke(request);
            }
        };
    }
    
    private ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodCache.get(method);
        if (result != null) {
            return result;
        }

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this, method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    /**
     * Method to enable or disable internal logger
     */
    public static void enableLogger(boolean enable) {
        Logger.sEnable = enable;
    }

    /**
     * Builder to create a new {@link AndLinker} instance.
     */
    public static final class Builder {
        
        private Context mContext;
        private String mPackageName;
        private String mAction;
        private String mClassName;
        private List<CallAdapter.Factory> mAdapterFactories = new ArrayList<>();
        
        public Builder(Context context) {
            mContext = context;
        }

        /**
         * Set the remote service package name.
         */
        public Builder packageName(String packageName) {
            mPackageName = packageName;
            return this;
        }

        /**
         * Set the action to bind the remote service.
         */
        public Builder action(String action) {
            mAction = action;
            return this;
        }

        /**
         * Set the class name of the remote service.
         */
        public Builder className(String className) {
            mClassName = className;
            return this;
        }

        /**
         * Add a call adapter factory for supporting service method return types other than {@link Call}.
         */
        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            mAdapterFactories.add(checkNotNull(factory, "factory == null"));
            return this;
        }

        /**
         * Create the {@link AndLinker} instance using the configured values.
         */
        public AndLinker build() {
            if (Utils.isStringBlank(mPackageName)) {
                throw new IllegalStateException("Package name required.");
            }
            if (Utils.isStringBlank(mAction) && Utils.isStringBlank(mClassName)) {
                throw new IllegalStateException("You must set one of the action or className.");
            }
            return new AndLinker(mContext, mPackageName, mAction, mClassName, mAdapterFactories);
        }
    }

    /**
     * Interface definition for a callback to be invoked when linker is bind or unBind to the service.
     */
    public interface BindCallback {

        /**
         * Called when a connection to the remote service has been established, now you can execute the remote call.
         */
        void onBind();

        /**
         * Called when a connection to the remote service has been lost, any remote call will not execute.
         */
        void onUnBind();
    }
}
