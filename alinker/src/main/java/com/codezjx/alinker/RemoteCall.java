package com.codezjx.alinker;

import android.os.RemoteException;
import android.util.Log;

import com.codezjx.alinker.model.Response;

import static com.codezjx.alinker.Utils.checkNotNull;

/**
 * Created by codezjx on 2017/10/14.<br/>
 */
public class RemoteCall implements Call<Object> {
    
    private static final String TAG = "RemoteCall";

    private final ITransfer mTransferService;
    private final ServiceMethod mServiceMethod;
    private final Object[] mArgs;
    private final Dispatcher mDispatcher;
    private volatile boolean mExecuted;
    private volatile boolean mCanceled;

    public RemoteCall(ITransfer transferService, ServiceMethod serviceMethod, Object[] args, Dispatcher dispatcher) {
        mTransferService = transferService;
        mServiceMethod = serviceMethod;
        mArgs = args;
        mDispatcher = dispatcher;
    }

    private Object executeInternal() throws RemoteException {
        @SuppressWarnings("unchecked")
        ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[]) mServiceMethod.getParameterHandlers();

        int argumentCount = mArgs != null ? mArgs.length : 0;
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount
                    + ") doesn't match expected count (" + handlers.length + ")");
        }

        RequestBuilder requestBuilder = new RequestBuilder(mServiceMethod.getClassName(), mServiceMethod.getMethodName(), argumentCount);
        for (int p = 0; p < argumentCount; p++) {
            handlers[p].apply(requestBuilder, mArgs[p], p);
        }
        
        Response response = mTransferService.execute(requestBuilder.build());
        Log.d(TAG, "Response from server, code:" + response.getStatusCode() + " msg:" + response.getStatusMessage());
        return response.getResult();
    }

    @Override
    public Object execute() {
        checkExecuted();
        if (mCanceled) {
            Log.w(TAG, "Already canceled");
            return null;
        }
        Object result = null;
        try {
            result = executeInternal();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void enqueue(Callback<Object> callback) {
        checkNotNull(callback, "callback == null");
        checkExecuted();
        if (mCanceled) {
            Log.w(TAG, "Already canceled");
            return;
        }
        
        mDispatcher.enqueue(new AsyncCall(callback));
    }

    private void checkExecuted() {
        synchronized (this) {
            if (mExecuted) {
                throw new IllegalStateException("Already executed.");
            }
            mExecuted = true;
        }
    }
    
    final class AsyncCall implements Runnable {
        
        private Callback<Object> mCallback;

        AsyncCall(Callback<Object> callback) {
            mCallback = callback;
        }

        @Override
        public void run() {
            try {
                Object result = executeInternal();
                mCallback.onResponse(RemoteCall.this, result);
            } catch (RemoteException e) {
                e.printStackTrace();
                mCallback.onFailure(RemoteCall.this, e);
            }
        }
        
    }

    @Override
    public synchronized boolean isExecuted() {
        return mExecuted;
    }

    @Override
    public void cancel() {
        mCanceled = true;
    }

    @Override
    public boolean isCanceled() {
        return mCanceled;
    }
    
}
