package com.example.andlinker;

import com.codezjx.andlinker.annotation.RemoteInterface;

import java.util.List;

/**
 * Created by codezjx on 2018/3/14.<br/>
 */
@RemoteInterface
public interface IRemoteTask {

    int remoteCalculate(int a, int b);

    List<ParcelableObj> getDatas();
    
}
