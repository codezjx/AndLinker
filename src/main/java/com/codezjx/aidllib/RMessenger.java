package com.codezjx.aidllib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by codezjx on 2017/9/14.<br/>
 */
public class RMessenger {

    private final Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();
    private ServiceConnection mServiceConnection;
    private Context mContext;
    private String mPackageName;
    private ITransfer mTransferService;
    
    private RMessenger(Context context, String packageName) {
        mContext = context;
        mPackageName = packageName;
        mServiceConnection = createServiceConnection();
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
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mTransferService = null;
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

        public RMessenger build() {
            return new RMessenger(mContext, mPackageName);
        }
        
    }
}
