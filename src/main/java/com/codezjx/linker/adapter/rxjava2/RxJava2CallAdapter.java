package com.codezjx.linker.adapter.rxjava2;

import com.codezjx.linker.Call;
import com.codezjx.linker.CallAdapter;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Created by codezjx on 2017/10/14.<br/>
 */
final class RxJava2CallAdapter<R> implements CallAdapter<R, Observable<R>> {

    private final Scheduler mScheduler;

    RxJava2CallAdapter(Scheduler scheduler) {
        mScheduler = scheduler;
    }

    @Override
    public Observable<R> adapt(Call<R> call) {
        Observable<R> observable = new CallExecuteObservable<>(call);

        if (mScheduler != null) {
            observable = observable.subscribeOn(mScheduler);
        }
        
        return observable;
    }

}