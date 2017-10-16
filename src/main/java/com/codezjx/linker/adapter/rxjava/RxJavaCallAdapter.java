package com.codezjx.linker.adapter.rxjava;

import com.codezjx.linker.Call;
import com.codezjx.linker.CallAdapter;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;


/**
 * Created by codezjx on 2017/10/16.<br/>
 */
final class RxJavaCallAdapter<R> implements CallAdapter<R, Observable<R>> {
    
    private final Scheduler mScheduler;

    RxJavaCallAdapter(Scheduler scheduler) {
        mScheduler = scheduler;
    }

    @Override
    public Observable<R> adapt(Call<R> call) {
        OnSubscribe<R> subscribe = new CallExecuteOnSubscribe<>(call);
        Observable<R> observable = Observable.create(subscribe);
        
        if (mScheduler != null) {
            observable = observable.subscribeOn(mScheduler);
        }

        return observable;
    }


}
