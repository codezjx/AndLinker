package com.codezjx.alinker.adapter.rxjava;

import com.codezjx.alinker.Call;

import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.Exceptions;

/**
 * Created by codezjx on 2017/10/16.<br/>
 */
final class CallExecuteOnSubscribe<T> implements OnSubscribe<T> {

    private final Call<T> mCall;

    CallExecuteOnSubscribe(Call<T> call) {
        mCall = call;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        CallArbiter<T> arbiter = new CallArbiter<>(mCall, subscriber);
        subscriber.add(arbiter);
        subscriber.setProducer(arbiter);

        T response;
        try {
            response = mCall.execute();
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            arbiter.emitError(t);
            return;
        }
        arbiter.emitResponse(response);
    }
}
