package com.example.andlinker;

import com.codezjx.andlinker.annotation.RemoteInterface;

/**
 * Created by codezjx on 2018/3/13.<br/>
 */
@RemoteInterface
public interface IRemoteCallback {

    void onStart();

    void onValueChange(int value);
}