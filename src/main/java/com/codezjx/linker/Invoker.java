package com.codezjx.linker;

import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.codezjx.linker.annotation.ClassName;
import com.codezjx.linker.annotation.MethodName;
import com.codezjx.linker.model.Request;
import com.codezjx.linker.model.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
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
    private final ConcurrentHashMap<String, Object> mObjects;
    private final ConcurrentHashMap<String, Method> mMethods;
    private final RemoteCallbackList<ICallback> mCallbackList;
    
    private Invoker() {
        mClassTypes = new ConcurrentHashMap<String, Class<?>>();
        mObjects = new ConcurrentHashMap<String, Object>();
        mMethods = new ConcurrentHashMap<String, Method>();
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

    public void registerObject(Object object) {
        Class<?>[] interfaces = object.getClass().getInterfaces();
        if (interfaces.length != 1) {
            throw new IllegalArgumentException("Remote object must extend just one interface.");
        }
        Class<?> clazz = interfaces[0];
        ClassName className = clazz.getAnnotation(ClassName.class);
        if (className != null) {
            mObjects.putIfAbsent(className.value(), object);
        }
        registerMethod(clazz);
    }

    public void unRegisterObject(Object object) {
        // TODO
    }

    public Response invoke(Request request) {
        Object object = getObject(request.getTargetClass());
        Method method = getMethod(request.getMethodName());
        Object[] args = request.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof CallbackWrapper) {
                int pid = Binder.getCallingPid();
                CallbackWrapper wrapper = (CallbackWrapper) args[i];
                Class<?> clazz = getClass(wrapper.getClassName());
                args[i] = getProxy(clazz, pid);
            }
        }
        Object result = null;
        try {
            result = method.invoke(object, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new Response(0, "Success:" + request.getMethodName(), result);
    }

    public RemoteCallbackList<ICallback> getCallbackList() {
        return mCallbackList;
    }

    private void registerMethod(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            MethodName methodName = method.getAnnotation(MethodName.class);
            if (methodName != null) {
                mMethods.putIfAbsent(methodName.value(), method);
            }
        }
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

    private Class<?> getClass(String className) {
        return mClassTypes.get(className);
    }

    private Object getObject(String className) {
        return mObjects.get(className);
    }

    private Method getMethod(String methodName) {
        return mMethods.get(methodName);
    }
    
}
