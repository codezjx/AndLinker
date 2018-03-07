package com.codezjx.andlinker.adapter;

import android.os.Handler;
import android.os.Looper;

import com.codezjx.andlinker.Call;
import com.codezjx.andlinker.CallAdapter;
import com.codezjx.andlinker.Callback;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

/**
 * A {@linkplain CallAdapter.Factory call adapter} which uses the original {@link Call}, just return as is.
 */
public class OriginalCallAdapterFactory extends CallAdapter.Factory {

    private Executor mCallbackExecutor;
    
    private OriginalCallAdapterFactory(Executor callbackExecutor) {
        mCallbackExecutor = callbackExecutor;
    }

    /**
     * Create {@link OriginalCallAdapterFactory} with default Android main thread executor.
     */
    public static OriginalCallAdapterFactory create() {
        return new OriginalCallAdapterFactory(new MainThreadExecutor());
    }

    /**
     * Create {@link OriginalCallAdapterFactory} with specify {@link Executor}
     * @param callbackExecutor The executor on which {@link Callback} methods are invoked
     *                         when returning {@link Call} from your service method.
     */
    public static OriginalCallAdapterFactory create(Executor callbackExecutor) {
        return new OriginalCallAdapterFactory(callbackExecutor);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations) {
        if (getRawType(returnType) != Call.class) {
            return null;
        }
        
        return new CallAdapter<Object, Call<?>>() {
            @Override public Call<Object> adapt(Call<Object> call) {
                // Return executor wrapper call
                return new ExecutorCallbackCall<>(mCallbackExecutor, call);
            }
        };
    }
    
    static final class ExecutorCallbackCall<T> implements Call<T> {
        final Executor mCallbackExecutor;
        final Call<T> mDelegate;

        ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
            mCallbackExecutor = callbackExecutor;
            mDelegate = delegate;
        }

        @Override
        public T execute() {
            return mDelegate.execute();
        }

        @Override
        public void enqueue(final Callback<T> callback) {
            if (callback == null) {
                throw new NullPointerException("callback == null");
            }
            mDelegate.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, final T response) {
                    mCallbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (mDelegate.isCanceled()) {
                                // Emulate behavior of throwing/delivering an IOException on cancellation.
                                callback.onFailure(ExecutorCallbackCall.this, new IllegalStateException("Already canceled"));
                            } else {
                                callback.onResponse(ExecutorCallbackCall.this, response);
                            }
                        }
                    });
                }
                
                @Override
                public void onFailure(Call<T> call, final Throwable t) {
                    mCallbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(ExecutorCallbackCall.this, t);
                        }
                    });
                }
            });
        }

        @Override
        public boolean isExecuted() {
            return mDelegate.isExecuted();
        }

        @Override
        public void cancel() {
            mDelegate.cancel();
        }

        @Override
        public boolean isCanceled() {
            return mDelegate.isCanceled();
        }
    }
    
    private static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override public void execute(Runnable r) {
            handler.post(r);
        }
    }
}
