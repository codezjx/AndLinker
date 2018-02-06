package com.codezjx.andlinker;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by codezjx on 2018/1/10.<br/>
 */
final class Dispatcher {

    private static final String THREAD_NAME = "Dispatcher Thread #";
    private static final int KEEP_ALIVE_TIME_SECONDS = 60;
    private ExecutorService mExecutorService;

    Dispatcher() {
        
    }

    Dispatcher(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    synchronized void enqueue(Runnable task) {
        if (mExecutorService == null) {
            mExecutorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    KEEP_ALIVE_TIME_SECONDS, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), createFactory());
        }
        mExecutorService.execute(task);
    }

    private ThreadFactory createFactory() {
        return new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);
            
            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                Thread thread = new Thread(runnable, THREAD_NAME + mCount.getAndIncrement());
                thread.setDaemon(false);
                return thread;
            }
        };
    }
    
}
