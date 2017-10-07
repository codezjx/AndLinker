package com.codezjx.aidllib;

import com.codezjx.aidllib.annotation.ClassName;
import com.codezjx.aidllib.annotation.MethodName;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by codezjx on 2017/10/3.<br/>
 */
public class Invoker {
    
    private static volatile Invoker sInstance;

    private final ConcurrentHashMap<String, Object> mObjects;
    private final ConcurrentHashMap<String, Method> mMethods;
    
    private Invoker() {
        mObjects = new ConcurrentHashMap<String, Object>();
        mMethods = new ConcurrentHashMap<String, Method>();
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

    public void registerObject(Object object) {
        Class<?> clazz = object.getClass();
        ClassName className = clazz.getAnnotation(ClassName.class);
        if (className != null) {
            mObjects.putIfAbsent(className.value(), object);
        }
        registerMethod(clazz);
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

    public Object getObject(String className) {
        return mObjects.get(className);
    }

    public Method getMethod(String methodName) {
        return mMethods.get(methodName);
    }
    
}
