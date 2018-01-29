package com.codezjx.andlinker;

import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.codezjx.andlinker.annotation.ClassName;
import com.codezjx.andlinker.annotation.MethodName;

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

    public void unRegisterClass(Class<?> clazz) {
        ClassName className = clazz.getAnnotation(ClassName.class);
        if (className != null) {
            mClassTypes.remove(className.value());
        }
    }

    private void handleObject(Object target, boolean isRegister) {
        if (target == null) {
            throw new NullPointerException("Object to (un)register must not be null.");
        }
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
            // The compiler sometimes creates synthetic bridge methods as part of the
            // type erasure process. As of JDK8 these methods now include the same
            // annotations as the original declarations. They should be ignored for
            // subscribe/produce.
            if (method.isBridge()) {
                continue;
            }
            MethodName methodNameAnnotation = method.getAnnotation(MethodName.class);
            if (methodNameAnnotation != null) {
                String clsName = classNameAnnotation.value();
                String methodName = methodNameAnnotation.value();
                String key = createMethodExecutorKey(clsName, methodName);
                if (isRegister) {
                    MethodExecutor executor = new MethodExecutor(target, method);
                    MethodExecutor preExecutor = mMethodExecutors.putIfAbsent(key, executor);
                    if (preExecutor != null) {
                        throw new IllegalStateException("Key conflict with class:" + clsName + " method:" + methodName
                                + ". Please try another class/method name with annotation @ClassName/@MethodName.");
                    }
                } else {
                    mMethodExecutors.remove(key);
                }
            }
        }
    }

    public void registerObject(Object target) {
        handleObject(target, true);
    }

    public void unRegisterObject(Object target) {
        handleObject(target, false);
    }

    public Response invoke(Request request) {
        BaseTypeWrapper[] wrappers = request.getArgsWrapper();
        Object[] args = new Object[wrappers.length];
        for (int i = 0; i < wrappers.length; i++) {
            // Assign the origin args parameter
            args[i] = wrappers[i].getParam();
            if (wrappers[i].getType() == BaseTypeWrapper.TYPE_CALLBACK) {
                int pid = Binder.getCallingPid();
                Class<?> clazz = getClass(((CallbackTypeWrapper) wrappers[i]).getClassName());
                args[i] = getCallbackProxy(clazz, pid);
            }
        }
        MethodExecutor executor = getMethodExecutor(request);
        if (executor == null) {
            String errMsg = String.format("The method '%s' you call was not exist!", request.getMethodName());
            return new Response(Response.STATUS_CODE_NOT_FOUND, errMsg, null);
        }
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
                                    if (response.getStatusCode() != Response.STATUS_CODE_SUCCESS) {
                                        Logger.e(TAG, "Execute remote callback fail: " + response.toString());
                                    }
                                } catch (RemoteException e) {
                                    Logger.e(TAG, "Error when execute callback!", e);
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
        BaseTypeWrapper[] wrappers = new BaseTypeWrapper[args.length];
        for (int i = 0; i < args.length; i++) {
            wrappers[i] = new InTypeWrapper(args[i], args[i].getClass());
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

    private String createMethodExecutorKey(String clsName, String methodName) {
        StringBuilder sb = new StringBuilder();
        sb.append(clsName)
            .append('-')
            .append(methodName);
        return sb.toString();
    }

    private MethodExecutor getMethodExecutor(Request request) {
        String key = createMethodExecutorKey(request.getTargetClass(), request.getMethodName());
        return mMethodExecutors.get(key);
    }
    
}
