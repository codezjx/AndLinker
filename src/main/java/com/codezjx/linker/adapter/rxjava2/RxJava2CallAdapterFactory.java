package com.codezjx.linker.adapter.rxjava2;

import com.codezjx.linker.CallAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Created by codezjx on 2017/10/14.<br/>
 */
public class RxJava2CallAdapterFactory extends CallAdapter.Factory {

    private final Scheduler mScheduler;

    private RxJava2CallAdapterFactory(Scheduler scheduler) {
        mScheduler = scheduler;
    }

    public static RxJava2CallAdapterFactory create() {
        return new RxJava2CallAdapterFactory(null);
    }

    public static RxJava2CallAdapterFactory createWithScheduler(Scheduler scheduler) {
        if (scheduler == null) throw new NullPointerException("scheduler == null");
        return new RxJava2CallAdapterFactory(scheduler);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations) {
        Class<?> rawType = getRawType(returnType);
        if (rawType != Observable.class) {
            return null;
        }
        return new RxJava2CallAdapter(mScheduler);
    }
    
}
