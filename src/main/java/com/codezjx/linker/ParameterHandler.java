package com.codezjx.linker;

import android.util.Log;

import com.codezjx.linker.annotation.ClassName;
import com.codezjx.linker.annotation.In;
import com.codezjx.linker.annotation.Inout;
import com.codezjx.linker.annotation.Out;

import java.lang.annotation.Annotation;

/**
 * Created by codezjx on 2017/9/17.<br/>
 */
public interface ParameterHandler<T> {

    void apply(RequestBuilder builder, T value, int index);

    final class ParamNameHandler<T> implements ParameterHandler<T> {

        String mParamName;
        Class<?> mParamType;

        public ParamNameHandler(String paramName, Class<?> paramType) {
            mParamName = paramName;
            mParamType = paramType;
        }

        @Override
        public void apply(RequestBuilder builder, T value, int index) {
            Log.d("ParamNameHandler", "ParameterHandler mParamName:" + mParamName + " mParamType:" + mParamType + " value:" + value);
        }

    }
    
    final class CallbackHandler<T> implements ParameterHandler<T> {

        Class<?> mParamType;

        public CallbackHandler(Class<?> paramType) {
            mParamType = paramType;
        }

        @Override
        public void apply(RequestBuilder builder, T value, int index) {
            Log.d("CallbackHandler", "ParameterHandler mParamType:" + mParamType + " value:" + value);
            ClassName annotation = mParamType.getAnnotation(ClassName.class);
            String className = (annotation != null) ? annotation.value() : "";
            if (StringUtils.isBlank(className)) {
                throw new IllegalArgumentException("Callback type must provide @ClassName");
            }
            ParameterWrapper wrapper = new ParameterWrapper(value);
            wrapper.setParamClass(className);
            wrapper.setCallback(true);
            builder.applyWrapper(index, wrapper);
        }

    }
    
    final class ParamDirectionHandler<T> implements ParameterHandler<T> {

        Annotation mAnnotation;
        Class<?> mParamType;

        public ParamDirectionHandler(Annotation annotation, Class<?> paramType) {
            mAnnotation = annotation;
            mParamType = paramType;
        }

        @Override
        public void apply(RequestBuilder builder, T value, int index) {
            Log.d("ParamDirectionHandler", " mParamType:" + mParamType + " value:" + value + " index:" + index);
//            boolean isPrimitive = mParamType.isPrimitive();
//            if (isPrimitive) {
//                return;
//            }
            int direction = ParameterWrapper.DIRECTION_IN;
            if (mAnnotation instanceof In) {
                direction = ParameterWrapper.DIRECTION_IN;
            } else if (mAnnotation instanceof Out) {
                direction = ParameterWrapper.DIRECTION_OUT;
            } else if (mAnnotation instanceof Inout) {
                direction = ParameterWrapper.DIRECTION_INOUT;
            }
            ParameterWrapper wrapper = new ParameterWrapper(value);
            wrapper.setParamDirection(direction);
            builder.applyWrapper(index, wrapper);
        }
    }
    
    final class DefaultParameterHandler<T> implements ParameterHandler<T> {

        Class<?> mParamType;

        public DefaultParameterHandler(Class<?> paramType) {
            mParamType = paramType;
        }

        @Override
        public void apply(RequestBuilder builder, T value, int index) {
            ParameterWrapper wrapper = new ParameterWrapper(value);
            int type = ParameterWrapper.TYPE_NULL;
            wrapper.setParamType(type);
            builder.applyWrapper(index, wrapper);
        }
    }
}
