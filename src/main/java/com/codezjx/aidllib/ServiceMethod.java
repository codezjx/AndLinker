package com.codezjx.aidllib;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.codezjx.aidllib.annotation.MethodName;
import com.codezjx.aidllib.annotation.ParamName;
import com.codezjx.aidllib.model.Request;
import com.codezjx.aidllib.model.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by codezjx on 2017/9/14.<br/>
 * Adapts an invocation of an interface method into an AIDL call.
 */
public class ServiceMethod {
    
    private static final String TAG = "ServiceMethod";

    private String mMethodName;
    private ParameterHandler<?>[] mParameterHandlers;

    public ServiceMethod(Builder builder) {
        mMethodName = builder.mMethodName;
        mParameterHandlers = builder.mParameterHandlers;
    }

    public Object invoke(ITransfer transfer, Object[] args) {
        @SuppressWarnings("unchecked")
        ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[]) mParameterHandlers;

        int argumentCount = args != null ? args.length : 0;
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount
                    + ") doesn't match expected count (" + handlers.length + ")");
        }

        Bundle bundle = new Bundle();
        for (int p = 0; p < argumentCount; p++) {
            handlers[p].apply(bundle, args[p]);
        }

        Response response = null;
        try {
            response = transfer.execute(new Request("", mMethodName, bundle));
            Log.d(TAG, "Response from server, code:" + response.getStatusCode() + " msg:" + response.getStatusMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static final class Builder {

        private Method mMethod;
        private Annotation[] mMethodAnnotations;
        private Annotation[][] mParameterAnnotationsArray;
        private Type[] mParameterTypes;

        private String mMethodName;
        private ParameterHandler<?>[] mParameterHandlers;
        
        public Builder(Method method) {
            mMethod = method;
            mMethodAnnotations = method.getAnnotations();
            mParameterAnnotationsArray = method.getParameterAnnotations();
            mParameterTypes = method.getGenericParameterTypes();
        }
        
        public ServiceMethod build() {

            for (Annotation annotation : mMethodAnnotations) {
                parseMethodAnnotation(annotation);
            }

            int parameterCount = mParameterAnnotationsArray.length;
            mParameterHandlers = new ParameterHandler<?>[parameterCount];
            for (int p = 0; p < parameterCount; p++) {
                Type parameterType = mParameterTypes[p];
                if (Utils.hasUnresolvableType(parameterType)) {
                    throw parameterError(p, "Parameter type must not include a type variable or wildcard: %s",
                            parameterType);
                }
                
                Annotation[] parameterAnnotations = mParameterAnnotationsArray[p];
                if (parameterAnnotations == null) {
                    throw parameterError(p, "No parameter annotation found.");
                }

                mParameterHandlers[p] = parseParameter(p, parameterType, parameterAnnotations);
            }
            
            return new ServiceMethod(this);
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof MethodName) {
                mMethodName = ((MethodName) annotation).value();
            }
        }

        private ParameterHandler<?>  parseParameter(int p, Type parameterType, Annotation[] annotations) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ParamName) {
                    String paramName = ((ParamName) annotation).value();
                    Class<?> rawParameterType = Utils.getRawType(parameterType);
                    return new ParameterHandler.ParamNameHandler<>(paramName, rawParameterType);
                }
            }
            throw parameterError(p, "No Retrofit annotation found.");
        }

        private RuntimeException methodError(String message, Object... args) {
            return methodError(null, message, args);
        }

        private RuntimeException methodError(Throwable cause, String message, Object... args) {
            message = String.format(message, args);
            return new IllegalArgumentException(message
                    + "\n    for method "
                    + mMethod.getDeclaringClass().getSimpleName()
                    + "."
                    + mMethod.getName(), cause);
        }

        private RuntimeException parameterError(
                Throwable cause, int p, String message, Object... args) {
            return methodError(cause, message + " (parameter #" + (p + 1) + ")", args);
        }

        private RuntimeException parameterError(int p, String message, Object... args) {
            return methodError(message + " (parameter #" + (p + 1) + ")", args);
        }

    }
    
}
