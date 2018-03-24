package com.example.andlinker;

import android.graphics.Rect;

import com.codezjx.andlinker.annotation.Callback;
import com.codezjx.andlinker.annotation.In;
import com.codezjx.andlinker.annotation.Inout;
import com.codezjx.andlinker.annotation.OneWay;
import com.codezjx.andlinker.annotation.Out;
import com.codezjx.andlinker.annotation.RemoteInterface;

/**
 * Created by codezjx on 2018/3/12.<br/>
 */
@RemoteInterface
public interface IRemoteService {

    int getPid();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                    double aDouble, String aString);

    void registerCallback(@Callback IRemoteCallback callback);

    void directionalParamMethod(@In int[] arr, @Out ParcelableObj obj, @Inout Rect rect);

    @OneWay
    void onewayMethod(String msg);
}