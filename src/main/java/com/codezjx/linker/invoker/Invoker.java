package com.codezjx.linker.invoker;

import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.codezjx.linker.ICallback;
import com.codezjx.linker.ParameterWrapper;
import com.codezjx.linker.Utils;
import com.codezjx.linker.annotation.ClassName;
import com.codezjx.linker.annotation.MethodName;
import com.codezjx.linker.model.Request;
import com.codezjx.linker.model.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by codezjx on 2017/10/3.<br/>
 */
public class Invoker {
    
    private static final String TAG = "Invoker";
    private static volatile Invoker sInstance;

    private final ConcurrentHashMap<String, Class<?>> mClassTypes;
    private final ConcurrentHashMap<String, MethodExecutor> mMethodExecutors;
    private final RemoteCallbackList<ICallback> mCallbackList;
    
    private Invoker() {
        mClassTypes = new ConcurrentHashMap<String, Class<?>>();
        mMethodExecutors = new ConcurrentHashMap<String, MethodExecutor>();
        mCallbackList = new RemoteCallbackList<ICallback>();
    }
    
    public static Invoker getInstance() {
        if (sInstance == null) {
            synchronized (Invoker.class) {
                if (sInstance == null) {
                    sInstance = new Invoker();
                }
            }
        }
        return sInstance;
    }

    public void registerClass(Class<?> clazz) {
        ClassName className = clazz.getAnnotation(ClassName.class);
        if (className != null) {
            mClassTypes.putIfAbsent(className.value(), clazz);
        }
    }

    public void registerObject(Object target) {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if (interfaces.length != 1) {
            throw new IllegalArgumentException("Remote object must extend just one interface.");
        }
        Class<?> clazz = interfaces[0];
        ClassName classNameAnnotation = clazz.getAnnotation(ClassName.class);
        if (classNameAnnotation == null) {
            throw new IllegalArgumentException("Interface doesn't has any annotation.");
        }
        // Cache all annotation method
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            MethodName methodNameAnnotation = method.getAnnotation(MethodName.class);
            if (methodNameAnnotation != null) {
                String key = classNameAnnotation.value() + methodNameAnnotation.value();
                MethodExecutor executor = new MethodExecutor(target, method);
                mMethodExecutors.putIfAbsent(key, executor);
            }
        }
    }

    public void unRegisterObject(Object object) {
        // TODO
    }

    public Response invoke(Request request) {
        ParameterWrapper[] wrappers = request.getArgsWrapper();
        Object[] args = new Object[wrappers.length];
        for (int i = 0; i < wrappers.length; i++) {
            // Assign the origin args parameter
            args[i] = wrappers[i].getParam();
            if (wrappers[i].isCallback()) {
                int pid = Binder.getCallingPid();
                Class<?> clazz = getClass(wrappers[i].getParamClass());
                args[i] = getCallbackProxy(clazz, pid);
            }
        }
        MethodExecutor executor = getMethodExecutor(request);
        return executor.execute(args);
    }

    public RemoteCallbackList<ICallback> getCallbackList() {
        return mCallbackList;
    }

    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    private <T> T getCallbackProxy(final Class<T> service, final int pid) {
        Utils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;
                        final int len = mCallbackList.beginBroadcast();
                        for (int i = 0; i < len; i++) {
                            int cookiePid = (int) mCallbackList.getBroadcastCookie(i);
                            if (cookiePid == pid) {
                                try {
                                    Request request = createCallbackRequest(parseClassName(service), parseMethodName(method), args);
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

    private Request createCallbackRequest(String targetClass, String methodName, Object[] args) {
        ParameterWrapper[] wrappers = new ParameterWrapper[args.length];
        for (int i = 0; i < args.length; i++) {
            wrappers[i] = new ParameterWrapper(args[i]);
        }
        return new Request(targetClass, methodName, wrappers);
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

    private Class<?> getClass(String className) {
        return mClassTypes.get(className);
    }

    private MethodExecutor getMethodExecutor(Request request) {
        String key = request.getTargetClass() + request.getMethodName();
        return mMethodExecutors.get(key);
    }
    
}
