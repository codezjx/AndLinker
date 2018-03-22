package com.codezjx.andlinker;

import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.codezjx.andlinker.annotation.RemoteInterface;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by codezjx on 2017/10/3.<br/>
 */
final class Invoker {
    
    private static final String TAG = "Invoker";

    private final ConcurrentHashMap<String, Class<?>> mCallbackClassTypes;
    private final ConcurrentHashMap<String, MethodExecutor> mMethodExecutors;
    private final RemoteCallbackList<ICallback> mCallbackList;
    
    Invoker() {
        mCallbackClassTypes = new ConcurrentHashMap<String, Class<?>>();
        mMethodExecutors = new ConcurrentHashMap<String, MethodExecutor>();
        mCallbackList = new RemoteCallbackList<ICallback>();
    }

    private void handleCallbackClass(Class<?> clazz, boolean isRegister) {
        if (!clazz.isAnnotationPresent(RemoteInterface.class)) {
            throw new IllegalArgumentException("Callback interface doesn't has @RemoteInterface annotation.");
        }
        String className = clazz.getSimpleName();
        if (isRegister) {
            mCallbackClassTypes.putIfAbsent(className, clazz);
        } else {
            mCallbackClassTypes.remove(className);
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
        if (!clazz.isAnnotationPresent(RemoteInterface.class)) {
            throw new IllegalArgumentException("Interface doesn't has @RemoteInterface annotation.");
        }
        // Cache all annotation method
        String clsName = clazz.getSimpleName();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            // The compiler sometimes creates synthetic bridge methods as part of the
            // type erasure process. As of JDK8 these methods now include the same
            // annotations as the original declarations. They should be ignored for
            // subscribe/produce.
            if (method.isBridge()) {
                continue;
            }
            String methodName = method.getName();
            String key = createMethodExecutorKey(clsName, methodName);
            if (isRegister) {
                MethodExecutor executor = new MethodExecutor(target, method);
                MethodExecutor preExecutor = mMethodExecutors.putIfAbsent(key, executor);
                if (preExecutor != null) {
                    throw new IllegalStateException("Key conflict with class:" + clsName + " method:" + methodName
                            + ". Please try another class/method name.");
                }
            } else {
                mMethodExecutors.remove(key);
            }
            // Cache callback class if exist
            Class<?>[] paramCls = method.getParameterTypes();
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < paramCls.length; i++) {
                Class<?> cls = paramCls[i];
                Annotation[] annotations = paramAnnotations[i];
                if (!containCallbackAnnotation(annotations)) {
                    continue;
                }
                handleCallbackClass(cls, isRegister);
            }
        }
    }

    private boolean containCallbackAnnotation(Annotation[] annotations) {
        if (annotations == null) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof com.codezjx.andlinker.annotation.Callback) {
                return true;
            }
        }
        return false;
    }

    void registerObject(Object target) {
        handleObject(target, true);
    }

    void unRegisterObject(Object target) {
        handleObject(target, false);
    }

    Response invoke(Request request) {
        BaseTypeWrapper[] wrappers = request.getArgsWrapper();
        Object[] args = new Object[wrappers.length];
        for (int i = 0; i < wrappers.length; i++) {
            // Assign the origin args parameter
            args[i] = wrappers[i].getParam();
            if (wrappers[i].getType() == BaseTypeWrapper.TYPE_CALLBACK) {
                int pid = Binder.getCallingPid();
                String clazzName = ((CallbackTypeWrapper) wrappers[i]).getClassName();
                Class<?> clazz = getCallbackClass(clazzName);
                if (clazz == null) {
                    throw new IllegalStateException("Can't find callback class: " + clazzName);
                }
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

    RemoteCallbackList<ICallback> getCallbackList() {
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
                                    Request request = createCallbackRequest(service.getSimpleName(), method.getName(), args);
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

    private Class<?> getCallbackClass(String className) {
        return mCallbackClassTypes.get(className);
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
