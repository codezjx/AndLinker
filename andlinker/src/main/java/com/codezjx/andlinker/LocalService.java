package com.codezjx.andlinker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * Created by codezjx on 2017/9/13.<br/>
 */
public class LocalService extends Service {
    
    private static final String TAG = "LocalService";
    private RemoteCallbackList<ICallback> mCallbackList;
    private Invoker mInvoker;

    @Override
    public void onCreate() {
        super.onCreate();
        mInvoker = Invoker.getInstance();
        mCallbackList = mInvoker.getCallbackList();
    }

    private ITransfer.Stub mBinder = new ITransfer.Stub() {
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
    };
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
