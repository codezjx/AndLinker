package com.codezjx.linker.adapter.rxjava2;

import com.codezjx.linker.Call;
import com.codezjx.linker.CallAdapter;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Created by codezjx on 2017/10/14.<br/>
 */
final class RxJava2CallAdapter<R> implements CallAdapter<R, Object> {

    private final Scheduler mScheduler;
    private final boolean mIsFlowable;

    RxJava2CallAdapter(Scheduler scheduler, boolean isFlowable) {
        mScheduler = scheduler;
        mIsFlowable = isFlowable;
    }

    @Override
    public Object adapt(Call<R> call) {
        Observable<R> observable = new CallExecuteObservable<>(call);

        if (mScheduler != null) {
            observable = observable.subscribeOn(mScheduler);
        }

        if (mIsFlowable) {
            return observable.toFlowable(BackpressureStrategy.LATEST);
        }
        
        return observable;
    }

}