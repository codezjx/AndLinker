package com.codezjx.aidllib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codezjx.aidllib.model.Request;
import com.codezjx.aidllib.model.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by codezjx on 2017/9/13.<br/>
 */
public class LocalService extends Service {
    
    private static final String TAG = "LocalService";
    private Invoker mInvoker;

    @Override
    public void onCreate() {
        super.onCreate();
        mInvoker = Invoker.getInstance();
    }

    private ITransfer.Stub mBinder = new ITransfer.Stub() {
        @Override
        public Response execute(Request request) throws RemoteException {
            for (Object param : request.getArgs()) {
                Log.d(TAG, "Receive param:" + " value:" + param + " class:" + param.getClass());
            }
            Log.d(TAG, "Receive request:" + request.getMethodName());
            Object object = mInvoker.getObject(request.getTargetClass());
            Method method = mInvoker.getMethod(request.getMethodName());
            Object result = null;
            try {
                result = method.invoke(object, request.getArgs());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return new Response(0, "Success:" + request.getMethodName(), result);
        }
    };
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
}
