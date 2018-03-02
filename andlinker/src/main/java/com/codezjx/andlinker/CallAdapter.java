package com.codezjx.andlinker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Adapts a {@link Call} with response type {@code R} into the type of {@code T}. Instances are
 * created by {@linkplain Factory factory} which is {@linkplain AndLinker.Builder#addCallAdapterFactory(Factory) installed}
 * into the {@link AndLinker} instance.
 */
public interface CallAdapter<R, T> {
    
    /**
     * Returns an instance of {@code T} which delegates to {@code call}.
     */
    T adapt(Call<R> call);

    /**
     * Creates {@link CallAdapter} instances based on the return type of {@linkplain
     * AndLinker#create(Class) the service interface} methods.
     */
    abstract class Factory {

        /**
         * Returns a call adapter for interface methods that return {@code returnType}, or null if it
         * cannot be handled by this factory.
         */
        public abstract CallAdapter<?, ?> get(Type returnType, Annotation[] annotations);

        protected static Class<?> getRawType(Type type) {
            return Utils.getRawType(type);
        }
        
    }
    
}
