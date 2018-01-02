package com.codezjx.alinker.adapter;

import com.codezjx.alinker.Call;
import com.codezjx.alinker.CallAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by codezjx on 2017/10/14.<br/>
 */
public class DefaultCallAdapterFactory extends CallAdapter.Factory {

    public static final CallAdapter.Factory INSTANCE = new DefaultCallAdapterFactory();

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations) {
        if (getRawType(returnType) != Call.class) {
            return null;
        }
        
        return new CallAdapter<Object, Call<?>>() {
            @Override public Call<Object> adapt(Call<Object> call) {
                // Return as is
                return call;
            }
        };
    }
    
}
