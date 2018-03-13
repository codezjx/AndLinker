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

import java.util.Arrays;

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service onBind()");
        return mLinkerBinder;
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
}
