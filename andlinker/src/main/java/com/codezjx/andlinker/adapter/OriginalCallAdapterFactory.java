package com.codezjx.andlinker.adapter;

import com.codezjx.andlinker.Call;
import com.codezjx.andlinker.CallAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by codezjx on 2017/10/14.<br/>
 */
public class OriginalCallAdapterFactory extends CallAdapter.Factory {

    public static OriginalCallAdapterFactory create() {
        return new OriginalCallAdapterFactory();
    }

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
