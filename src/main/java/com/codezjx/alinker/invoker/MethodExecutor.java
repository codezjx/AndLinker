package com.codezjx.alinker.invoker;

import com.codezjx.alinker.model.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by codezjx on 2017/10/22.<br/>
 */
class MethodExecutor {
    
    /** Target object to invoke. */
    private final Object mTarget;
    /** Method to execute. */
    private final Method mMethod;

    MethodExecutor(Object target, Method method) {
        if (target == null) {
            throw new NullPointerException("Target cannot be null.");
        }
        if (method == null) {
            throw new NullPointerException("Method cannot be null.");
        }

        mTarget = target;
        mMethod = method;
        method.setAccessible(true);
    }

    public Response execute(Object[] args) {
        Object result = null;
        try {
            result = mMethod.invoke(mTarget, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new Response(0, "Success:" + mMethod.getName(), result);
    }
    
}
