package com.codezjx.linker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.codezjx.linker.model.Request;
import com.codezjx.linker.model.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private ITransfer mTransferService;
    private ICallback mCallback;
    
    private Linker(Context context, String packageName) {
        mInvoker = Invoker.getInstance();
        mContext = context;
        mPackageName = packageName;
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
                        return serviceMethod.invoke(mTransferService, args);
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
                Object object = mInvoker.getObject(request.getTargetClass());
                Method method = mInvoker.getMethod(request.getMethodName());
                Object result = null;
                try {
                    result = method.invoke(object, request.getArgs());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return new Response(0, "Success:" + request.getMethodName(), result);
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
                result = new ServiceMethod.Builder(method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    public static final class Builder {
        
        private Context mContext;
        private String mPackageName;
        
        public Builder(Context context) {
            mContext = context;
        }

        public Builder packageName(String packageName) {
            mPackageName = packageName;
            return this;
        }

        public Linker build() {
            return new Linker(mContext, mPackageName);
        }
        
    }
}
