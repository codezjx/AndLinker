package com.example.andlinker;

import android.app.Service;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.codezjx.andlinker.AndLinkerBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoteService extends Service {

    private static final String TAG = "RemoteService";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AndLinkerBinder mLinkerBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate()");
        mLinkerBinder = AndLinkerBinder.Factory.newBinder();
        mLinkerBinder.registerObject(mRemoteService);
        mLinkerBinder.registerObject(mRemoteTask);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service onBind()");
        return mLinkerBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy()");
        mLinkerBinder.unRegisterObject(mRemoteService);
        mLinkerBinder.unRegisterObject(mRemoteTask);
    }

    private final IRemoteService mRemoteService = new IRemoteService() {

        @Override
        public int getPid() {
            return android.os.Process.myPid();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean,
                               float aFloat, double aDouble, String aString) {
            final String msg = "Execute basicTypes() in server: " + anInt + ", " + aLong + ", " + aBoolean
                    + ", " + aFloat + ", " + aDouble + ", " + aString;
            mHandler.post(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show());
        }

        @Override
        public void registerCallback(IRemoteCallback callback) {
            callback.onStart();
            callback.onValueChange(1234);
        }

        @Override
        public void directionalParamMethod(int[] arr, ParcelableObj obj, Rect rect) {
            Log.d(TAG, "directionalParamMethod @In param: " + Arrays.toString(arr));
            Log.d(TAG, "directionalParamMethod @Out param: " + obj);
            Log.d(TAG, "directionalParamMethod @Inout param: " + rect);
            // Rewrite @Out and @Inout parameter
            obj.setType(123);
            obj.setValue(43.21f);
            obj.setMsg("Message from server");
            rect.set(100, 200, 300, 400);
        }

        @Override
        public void onewayMethod(String msg) {
            Log.d(TAG, "Call oneway method: " + msg);
            // Try to block method
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "After oneway method server.");
        }
    };
    
    private final IRemoteTask mRemoteTask = new IRemoteTask() {
        @Override
        public int remoteCalculate(int a, int b) {
            Log.d(TAG, "Call remoteCalculate(): " + a + ", " + b);
            // Simulate slow task
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return (a + b) * 100;
        }

        @Override
        public List<ParcelableObj> getDatas() {
            Log.d(TAG, "Call getDatas().");
            // Simulate slow task
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<ParcelableObj> datas = new ArrayList<>();
            datas.add(new ParcelableObj(1, 11.1f, "hello"));
            datas.add(new ParcelableObj(2, 22.2f, "world"));
            return datas;
        }
    };
}
