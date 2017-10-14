package com.codezjx.linker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by codezjx on 2017/10/14.<br/>
 */
public interface CallAdapter<R, T> {
    
    T adapt(Call<R> call);

    abstract class Factory {

        public abstract CallAdapter<?, ?> get(Type returnType, Annotation[] annotations);

        protected static Class<?> getRawType(Type type) {
            return Utils.getRawType(type);
        }
        
    }
    
}
