package com.codezjx.linker;

/**
 * Created by codezjx on 2017/10/14.<br/>
 */
public interface Call<T> {
    
    T execute();

    void executeAsync(Callback<T> callback);

    boolean isExecuted();

    void cancel();

    boolean isCanceled();
    
}
