package com.codezjx.aidllib;

import android.util.Log;

import com.codezjx.aidllib.annotation.ClassName;

/**
 * Created by codezjx on 2017/9/17.<br/>
 */
public interface ParameterHandler<T> {

    void apply(Object[] args, int index);

    final class ParamNameHandler<T> implements ParameterHandler<T> {

        String mParamName;
        Class<?> mParamType;

        public ParamNameHandler(String paramName, Class<?> paramType) {
            mParamName = paramName;
            mParamType = paramType;
        }

        @Override
        public void apply(Object[] args, int index) {
            Log.d("ParamNameHandler", "ParameterHandler mParamName:" + mParamName + " mParamType:" + mParamType + " value:" + args[index]);
        }

    }
    
    final class CallbackHandler<T> implements ParameterHandler<T> {

        Class<?> mParamType;

        public CallbackHandler(Class<?> paramType) {
            mParamType = paramType;
        }

        @Override
        public void apply(Object[] args, int index) {
            Log.d("CallbackHandler", "ParameterHandler mParamType:" + mParamType + " value:" + args[index]);
            ClassName annotation = mParamType.getAnnotation(ClassName.class);
            String className = (annotation != null) ? annotation.value() : "";
            if (StringUtils.isBlank(className)) {
                throw new IllegalArgumentException("Callback type must provide @ClassName");
            }
            args[index] = new CallbackWrapper(className);
        }

    }
}
