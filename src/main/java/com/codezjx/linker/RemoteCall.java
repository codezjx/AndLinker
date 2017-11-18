package com.codezjx.linker;

import android.os.RemoteException;
import android.util.Log;

import com.codezjx.linker.model.Response;

/**
 * Created by codezjx on 2017/10/14.<br/>
 */
public class RemoteCall implements Call<Object> {
    
    private static final String TAG = "RemoteCall";

    private final ITransfer mTransferService;
    private final ServiceMethod mServiceMethod;
    private final Object[] mArgs;

    public RemoteCall(ITransfer transferService, ServiceMethod serviceMethod, Object[] args) {
        mTransferService = transferService;
        mServiceMethod = serviceMethod;
        mArgs = args;
    }

    @Override
    public Object execute() {
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

        Object result = null;
        try {
            Response response = mTransferService.execute(requestBuilder.build());
            result = response.getResult();
            Log.d(TAG, "Response from server, code:" + response.getStatusCode() + " msg:" + response.getStatusMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void executeAsync(Callback<Object> callback) {

    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }
    
}
