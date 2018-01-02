package com.codezjx.alinker.adapter.rxjava2;

import com.codezjx.alinker.CallAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Flowable;
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
        boolean isFlowable = (rawType == Flowable.class);
        if (rawType != Observable.class && !isFlowable) {
            return null;
        }

        if (!(returnType instanceof ParameterizedType)) {
            String name = isFlowable ? "Flowable" : "Observable";
            throw new IllegalStateException(name + " return type must be parameterized"
                    + " as " + name + "<Foo> or " + name + "<? extends Foo>");
        }
        
        return new RxJava2CallAdapter(mScheduler, isFlowable);
    }
    
}
