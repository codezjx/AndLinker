package com.codezjx.andlinker;

import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

/**
 * Created by codezjx on 2017/9/13.<br/>
 */
final class LinkerBinder extends ITransfer.Stub implements ILinkerBinder {
    
    private static final String TAG = "LinkerBinder";
    private RemoteCallbackList<ICallback> mCallbackList;
    private Invoker mInvoker;

    LinkerBinder() {
        mInvoker = new Invoker();
        mCallbackList = mInvoker.getCallbackList();
    }

    @Override
    public void registerCallbackClass(Class<?> clazz) {
        mInvoker.registerCallbackClass(clazz);
    }

    @Override
    public void unRegisterCallbackClass(Class<?> clazz) {
        mInvoker.unRegisterCallbackClass(clazz);
    }

    @Override
    public void registerObject(Object target) {
        mInvoker.registerObject(target);
    }

    @Override
    public void unRegisterObject(Object target) {
        mInvoker.unRegisterObject(target);
    }
    
    @Override
    public Response execute(Request request) throws RemoteException {
        for (BaseTypeWrapper wrapper : request.getArgsWrapper()) {
            Logger.d(TAG, "Receive param, value:" + wrapper.getParam()
                    + " type:" + (wrapper.getParam() != null ? wrapper.getParam().getClass() : "null"));
        }
        Logger.d(TAG, "Receive request:" + request.getMethodName());
        return mInvoker.invoke(request);
    }

    @Override
    public void register(ICallback callback) throws RemoteException {
        int pid = Binder.getCallingPid();
        Logger.d(TAG, "register callback:" + callback + " pid:" + pid);
        if (callback != null) {
            mCallbackList.register(callback, pid);
        }
    }

    @Override
    public void unRegister(ICallback callback) throws RemoteException {
        int pid = Binder.getCallingPid();
        Logger.d(TAG, "unRegister callback:" + callback + " pid:" + pid);
        if (callback != null) {
            mCallbackList.unregister(callback);
        }
    }
}
