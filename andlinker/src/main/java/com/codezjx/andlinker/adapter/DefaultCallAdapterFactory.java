package com.codezjx.andlinker.adapter;

import com.codezjx.andlinker.Call;
import com.codezjx.andlinker.CallAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Default {@linkplain CallAdapter.Factory call adapter} which adapt {@link Call} to the execute result.
 */
public class DefaultCallAdapterFactory extends CallAdapter.Factory {

    public static final CallAdapter.Factory INSTANCE = new DefaultCallAdapterFactory();

    @Override
    public CallAdapter<?, ?> get(final Type returnType, Annotation[] annotations) {
        return new CallAdapter<Object, Object>() {
            @Override
            public Object adapt(Call<Object> call) {
                Class<?> rawType = getRawType(returnType);
                Object result = call.execute();
                if (result == null) {
                    result = createDefaultResult(rawType);
                }
                // Return the result
                return result;
            }
        };
    }

    private Object createDefaultResult(Class<?> returnType) {
        // For java.lang.NullPointerException: Expected to unbox a 'xxx' primitive type but was returned null
        // Visit https://github.com/codezjx/AndLinker/issues/14
        if (returnType == byte.class) {
            return (byte) 0;
        } else if (returnType == short.class) {
            return (short) 0;
        } else if (returnType == int.class || returnType == long.class || returnType == float.class || returnType == double.class) {
            return 0;
        } else if (returnType == boolean.class) {
            return false;
        } else if (returnType == char.class) {
            return ' ';
        }
        return null;
    }
    
}
