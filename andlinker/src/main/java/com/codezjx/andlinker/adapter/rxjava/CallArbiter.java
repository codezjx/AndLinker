package com.codezjx.andlinker.adapter.rxjava;

import com.codezjx.andlinker.Call;

import java.util.concurrent.atomic.AtomicInteger;

import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.exceptions.OnCompletedFailedException;
import rx.exceptions.OnErrorFailedException;
import rx.exceptions.OnErrorNotImplementedException;
import rx.plugins.RxJavaPlugins;

/**
 * Created by codezjx on 2017/10/16.<br/>
 */
final class CallArbiter<T> extends AtomicInteger implements Subscription, Producer {
    private static final int STATE_WAITING = 0;
    private static final int STATE_REQUESTED = 1;
    private static final int STATE_HAS_RESPONSE = 2;
    private static final int STATE_TERMINATED = 3;

    private final Call<T> mCall;
    private final Subscriber<? super T> mSubscriber;

    private volatile T mResponse;

    CallArbiter(Call<T> call, Subscriber<? super T> subscriber) {
        super(STATE_WAITING);

        mCall = call;
        mSubscriber = subscriber;
    }

    @Override
    public void unsubscribe() {
        mCall.cancel();
    }

    @Override
    public boolean isUnsubscribed() {
        return mCall.isCanceled();
    }

    @Override
    public void request(long amount) {
        if (amount == 0) {
            return;
        }
        while (true) {
            int state = get();
            switch (state) {
                case STATE_WAITING:
                    if (compareAndSet(STATE_WAITING, STATE_REQUESTED)) {
                        return;
                    }
                    break; // State transition failed. Try again.

                case STATE_HAS_RESPONSE:
                    if (compareAndSet(STATE_HAS_RESPONSE, STATE_TERMINATED)) {
                        deliverResponse(mResponse);
                        return;
                    }
                    break; // State transition failed. Try again.

                case STATE_REQUESTED:
                case STATE_TERMINATED:
                    return; // Nothing to do.

                default:
                    throw new IllegalStateException("Unknown state: " + state);
            }
        }
    }

    void emitResponse(T response) {
        while (true) {
            int state = get();
            switch (state) {
                case STATE_WAITING:
                    this.mResponse = response;
                    if (compareAndSet(STATE_WAITING, STATE_HAS_RESPONSE)) {
                        return;
                    }
                    break; // State transition failed. Try again.

                case STATE_REQUESTED:
                    if (compareAndSet(STATE_REQUESTED, STATE_TERMINATED)) {
                        deliverResponse(response);
                        return;
                    }
                    break; // State transition failed. Try again.

                case STATE_HAS_RESPONSE:
                case STATE_TERMINATED:
                    throw new AssertionError();

                default:
                    throw new IllegalStateException("Unknown state: " + state);
            }
        }
    }

    private void deliverResponse(T response) {
        try {
            if (!isUnsubscribed()) {
                mSubscriber.onNext(response);
            }
        } catch (OnCompletedFailedException
                | OnErrorFailedException
                | OnErrorNotImplementedException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            return;
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            try {
                mSubscriber.onError(t);
            } catch (OnCompletedFailedException
                    | OnErrorFailedException
                    | OnErrorNotImplementedException e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            } catch (Throwable inner) {
                Exceptions.throwIfFatal(inner);
                CompositeException composite = new CompositeException(t, inner);
                RxJavaPlugins.getInstance().getErrorHandler().handleError(composite);
            }
            return;
        }
        try {
            if (!isUnsubscribed()) {
                mSubscriber.onCompleted();
            }
        } catch (OnCompletedFailedException
                | OnErrorFailedException
                | OnErrorNotImplementedException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            RxJavaPlugins.getInstance().getErrorHandler().handleError(t);
        }
    }

    void emitError(Throwable t) {
        set(STATE_TERMINATED);

        if (!isUnsubscribed()) {
            try {
                mSubscriber.onError(t);
            } catch (OnCompletedFailedException
                    | OnErrorFailedException
                    | OnErrorNotImplementedException e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            } catch (Throwable inner) {
                Exceptions.throwIfFatal(inner);
                CompositeException composite = new CompositeException(t, inner);
                RxJavaPlugins.getInstance().getErrorHandler().handleError(composite);
            }
        }
    }
}
