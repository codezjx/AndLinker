package com.codezjx.andlinker;

import com.codezjx.andlinker.annotation.Callback;
import com.codezjx.andlinker.annotation.In;
import com.codezjx.andlinker.annotation.Inout;
import com.codezjx.andlinker.annotation.OneWay;
import com.codezjx.andlinker.annotation.Out;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by codezjx on 2017/9/14.<br/>
 * Adapts an invocation of an interface method into an AIDL call.
 */
final class ServiceMethod {
    
    private static final String TAG = "ServiceMethod";

    private CallAdapter mCallAdapter;
    private String mClassName;
    private String mMethodName;
    private boolean mOneWay; 
    private ParameterHandler<?>[] mParameterHandlers;

    ServiceMethod(Builder builder) {
        mCallAdapter = builder.mCallAdapter;
        mClassName = builder.mClassName;
        mMethodName = builder.mMethodName;
        mOneWay = builder.mOneWay;
        mParameterHandlers = builder.mParameterHandlers;
    }

    CallAdapter getCallAdapter() {
        return mCallAdapter;
    }

    String getClassName() {
        return mClassName;
    }

    String getMethodName() {
        return mMethodName;
    }

    boolean isOneWay() {
        return mOneWay;
    }

    ParameterHandler<?>[] getParameterHandlers() {
        return mParameterHandlers;
    }

    static final class Builder {

        private AndLinker mLinker;
        private Method mMethod;
        private Annotation[] mMethodAnnotations;
        private Annotation[][] mParameterAnnotationsArray;
        private Type[] mParameterTypes;

        private CallAdapter mCallAdapter;
        private String mClassName = "";
        private String mMethodName = "";
        private boolean mOneWay = false;
        private ParameterHandler<?>[] mParameterHandlers;

        Builder(AndLinker linker, Method method) {
            mLinker = linker;
            mMethod = method;
            mMethodAnnotations = method.getAnnotations();
            mParameterAnnotationsArray = method.getParameterAnnotations();
            mParameterTypes = method.getGenericParameterTypes();
        }
        
        ServiceMethod build() {
            mCallAdapter = createCallAdapter();
            mClassName = mMethod.getDeclaringClass().getSimpleName();
            mMethodName = mMethod.getName();

            for (Annotation annotation : mMethodAnnotations) {
                if (annotation instanceof OneWay) {
                    mOneWay = true;
                    break;
                }
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
                mParameterHandlers[p] = parseParameter(p, parameterType, parameterAnnotations);
            }
            
            return new ServiceMethod(this);
        }

        private CallAdapter createCallAdapter() {
            Type returnType = mMethod.getGenericReturnType();
            if (Utils.hasUnresolvableType(returnType)) {
                throw methodError(
                        "Method return type must not include a type variable or wildcard: %s", returnType);
            }
            Annotation[] annotations = mMethod.getAnnotations();
            try {
                //noinspection unchecked
                return mLinker.findCallAdapter(returnType, annotations);
            } catch (RuntimeException e) { // Wide exception range because factories are user code.
                throw methodError(e, "Unable to create call adapter for %s", returnType);
            }
        }

        private ParameterHandler<?>  parseParameter(int p, Type parameterType, Annotation[] annotations) {
            Class<?> rawParameterType = Utils.getRawType(parameterType);
            if (annotations == null || annotations.length == 0) {
                return new ParameterHandler.DefaultParameterHandler<>(rawParameterType);
            }
            for (Annotation annotation : annotations) {
                if (annotation instanceof Callback) {
                    return new ParameterHandler.CallbackHandler<>(rawParameterType);
                } else if (annotation instanceof In || annotation instanceof Out || annotation instanceof Inout) {
                    return new ParameterHandler.ParamDirectionHandler<>(annotation, rawParameterType);
                }
            }
            throw parameterError(p, "No support annotation found.");
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
