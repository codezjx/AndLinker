package com.codezjx.andlinker;

/**
 * Interface definition for a callback to be invoked when execute {@linkplain Call#enqueue(Callback) asynchronously call}.
 */
public interface Callback<T> {
    
    /**
     * Invoked for a received response.
     */
    void onResponse(Call<T> call, T response);
    
    /**
     * Invoked when a remote exception occurred.
     */
    void onFailure(Call<T> call, Throwable t);
    
}
