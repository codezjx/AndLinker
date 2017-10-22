package com.codezjx.linker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codezjx.linker.model.Request;
import com.codezjx.linker.model.Response;

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
            for (Object param : request.getArgs()) {
                Log.d(TAG, "Receive param:" + " value:" + param + " class:" + param.getClass());
            }
            Log.d(TAG, "Receive request:" + request.getMethodName());
            return mInvoker.invoke(request);
        }

        @Override
        public void register(ICallback callback) throws RemoteException {
            int pid = Binder.getCallingPid();
            Log.d(TAG, "register callback:" + callback + " pid:" + pid);
            if (callback != null) {
                mCallbackList.register(callback, pid);
            }
        }

        @Override
        public void unRegister(ICallback callback) throws RemoteException {
            int pid = Binder.getCallingPid();
            Log.d(TAG, "unRegister callback:" + callback + " pid:" + pid);
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
