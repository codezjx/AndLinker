package com.example.andlinker;

import com.codezjx.andlinker.annotation.ClassName;
import com.codezjx.andlinker.annotation.MethodName;

/**
 * Created by codezjx on 2018/3/13.<br/>
 */
@ClassName("com.example.andlinker.IRemoteCallback")
public interface IRemoteCallback {

    @MethodName("onValueChange")
    void onValueChange(int value);
}