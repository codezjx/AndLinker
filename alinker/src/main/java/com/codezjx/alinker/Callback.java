package com.codezjx.alinker;

/**
 * Created by codezjx on 2017/10/14.<br/>
 */
public interface Callback<T> {
    
    void onResponse(Call<T> call, T response);
    
    void onFailure(Call<T> call, Throwable t);
    
}
