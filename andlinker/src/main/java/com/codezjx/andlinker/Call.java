package com.codezjx.andlinker;

/**
 * An invocation of a remote method that sends a request to host and returns a response.
 */
public interface Call<T> {
    
    /**
     * Synchronously send the request and return its response.
     */
    T execute();

    /**
     * Asynchronously send the request and notify when response return.
     * @param callback The callback to notify.
     */
    void enqueue(Callback<T> callback);

    /**
     * Returns true if this call has been either {@linkplain #execute() executed} or {@linkplain #enqueue(Callback) enqueued}.
     */
    boolean isExecuted();

    /**
     * Cancel this call. If the call has not yet been executed it never will be.
     */
    void cancel();

    /**
     * True if {@link #cancel()} was called.
     */
    boolean isCanceled();
    
}
