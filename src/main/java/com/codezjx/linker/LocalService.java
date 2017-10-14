package com.codezjx.linker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codezjx.linker.annotation.ClassName;
import com.codezjx.linker.annotation.MethodName;
import com.codezjx.linker.model.Request;
import com.codezjx.linker.model.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by codezjx on 2017/9/13.<br/>
 */
public class LocalService extends Service {
    
    private static final String TAG = "LocalService";
    private RemoteCallbackList<ICallback> mCallbackList;
    private Invoker mInvoker;

    @Override
    public void onCreate() {
        super.onCreate();
        mInvoker = Invoker.getInstance();
        mCallbackList = new RemoteCallbackList<ICallback>();
    }

    private ITransfer.Stub mBinder = new ITransfer.Stub() {
        @Override
        public Response execute(Request request) throws RemoteException {
            for (Object param : request.getArgs()) {
                Log.d(TAG, "Receive param:" + " value:" + param + " class:" + param.getClass());
            }
            Log.d(TAG, "Receive request:" + request.getMethodName());
            Object object = mInvoker.getObject(request.getTargetClass());
            Method method = mInvoker.getMethod(request.getMethodName());
            Object[] args = request.getArgs();
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof CallbackWrapper) {
                    int pid = Binder.getCallingPid();
                    CallbackWrapper wrapper = (CallbackWrapper) args[i];
                    Class<?> clazz = mInvoker.getClass(wrapper.getClassName());
                    args[i] = getProxy(clazz, pid);
                }
            }
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

        @Override
        public void register(ICallback callback) throws RemoteException {
            int pid = Binder.getCallingPid();
            Log.d(TAG, "register callback:" + callback + " pid:" + pid);
            if (callback != null) {
                mCallbackList.register(callback, pid);
            }
        }

        @Override
        public void unRegister(ICallback callback) throws RemoteException {
            int pid = Binder.getCallingPid();
            Log.d(TAG, "unRegister callback:" + callback + " pid:" + pid);
            if (callback != null) {
                mCallbackList.unregister(callback);
            }
        }
    };
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    private <T> T getProxy(final Class<T> service, final int pid) {
        Utils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;
                        final int len = mCallbackList.beginBroadcast();
                        for (int i = 0; i < len; i++) {
                            int cookiePid = (int) mCallbackList.getBroadcastCookie(i);
                            if (cookiePid == pid) {
                                try {
                                    Request request = new Request(parseClassName(service), parseMethodName(method), args);
                                    Response response = mCallbackList.getBroadcastItem(i).callback(request);
                                    result = response.getResult();
                                    Log.d(TAG, "Execute remote callback:" + response.toString());
                                } catch (RemoteException e) {
                                    Log.e(TAG, "Error when execute callback!", e);
                                }
                                break;
                            }
                        }
                        mCallbackList.finishBroadcast();
                        return result;
                    }
                });
    }

    private String parseClassName(Class<?> clazz) {
        ClassName className = clazz.getAnnotation(ClassName.class);
        String classNameStr = "";
        if (className != null) {
            classNameStr = className.value();
        }
        return classNameStr;
    }

    private String parseMethodName(Method method) {
        MethodName methodName = method.getAnnotation(MethodName.class);
        String methodNameStr = "";
        if (methodName != null) {
            methodNameStr = methodName.value();
        }
        return methodNameStr;
    }
}
