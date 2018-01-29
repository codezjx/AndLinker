package com.codezjx.alinker.adapter;

import com.codezjx.alinker.Call;
import com.codezjx.alinker.CallAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by codezjx on 2018/1/10.<br/>
 */
public class DefaultCallAdapterFactory extends CallAdapter.Factory {

    public static final CallAdapter.Factory INSTANCE = new DefaultCallAdapterFactory();

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations) {
        return new CallAdapter<Object, Object>() {
            @Override
            public Object adapt(Call<Object> call) {
                // Return the result
                return call.execute();
            }
        };
    }
    
}
