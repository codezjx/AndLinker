package com.example.andlinker;

import android.graphics.Rect;

import com.codezjx.andlinker.annotation.Callback;
import com.codezjx.andlinker.annotation.ClassName;
import com.codezjx.andlinker.annotation.In;
import com.codezjx.andlinker.annotation.Inout;
import com.codezjx.andlinker.annotation.MethodName;
import com.codezjx.andlinker.annotation.OneWay;
import com.codezjx.andlinker.annotation.Out;

/**
 * Created by codezjx on 2018/3/12.<br/>
 */
@ClassName("com.example.andlinker.IRemoteService")
public interface IRemoteService {

    @MethodName("getPid")
    int getPid();

    @MethodName("basicTypes")
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                    double aDouble, String aString);

    @MethodName("registerCallback")
    void registerCallback(@Callback IRemoteCallback callback);

    @MethodName("directionalParamMethod")
    void directionalParamMethod(@In int[] arr, @Out ParcelableObj obj, @Inout Rect rect);

    @MethodName("onewayMethod")
    @OneWay
    void onewayMethod(String msg);
}