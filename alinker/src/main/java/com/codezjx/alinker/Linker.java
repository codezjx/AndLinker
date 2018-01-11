package com.codezjx.alinker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.codezjx.alinker.adapter.DefaultCallAdapterFactory;
import com.codezjx.alinker.invoker.Invoker;
import com.codezjx.alinker.model.Request;
import com.codezjx.alinker.model.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.codezjx.alinker.Utils.checkNotNull;

/**
 * Created by codezjx on 2017/9/14.<br/>
 */
public class Linker {
    
    private static final String TAG = "RMessenger";

    private final Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();
    private ServiceConnection mServiceConnection;
    private Invoker mInvoker;
    private Context mContext;
    private String mPackageName;
    private List<CallAdapter.Factory> mAdapterFactories;
    private Dispatcher mDispatcher;
    private ITransfer mTransferService;
    private ICallback mCallback;
    
    private Linker(Context context, String packageName, List<CallAdapter.Factory> adapterFactories) {
        mInvoker = Invoker.getInstance();
        mContext = context;
        mPackageName = packageName;
        mAdapterFactories = adapterFactories;
        mDispatcher = new Dispatcher();
        mServiceConnection = createServiceConnection();
        mCallback = createCallback();
    }

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

    public void bind() {
        bind(null);
    }

    public void bind(Class<? extends LocalService> service) {
        Intent intent = new Intent();
        String serviceName = (service != null) ? service.getName() : LocalService.class.getName();
        intent.setClassName(mPackageName, serviceName);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
    public void unbind() {
        mContext.unbindService(mServiceConnection);
    }

    public CallAdapter<?, ?> findCallAdapter(Type returnType, Annotation[] annotations) {
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
                mTransferService = ITransfer.Stub.asInterface(service);
                try {
                    mTransferService.register(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                try {
                    mTransferService.unRegister(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mTransferService = null;
            }
        };
    }

    private ICallback createCallback() {
        return new ICallback.Stub() {
            @Override
            public Response callback(Request request) throws RemoteException {
                Log.d(TAG, "Receive callback in client:" + request.toString());
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

    public static final class Builder {
        
        private Context mContext;
        private String mPackageName;
        private List<CallAdapter.Factory> mAdapterFactories = new ArrayList<>();
        
        public Builder(Context context) {
            mContext = context;
        }

        public Builder packageName(String packageName) {
            mPackageName = packageName;
            return this;
        }

        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            mAdapterFactories.add(checkNotNull(factory, "factory == null"));
            return this;
        }

        public Linker build() {
            return new Linker(mContext, mPackageName, mAdapterFactories);
        }
        
    }
}
