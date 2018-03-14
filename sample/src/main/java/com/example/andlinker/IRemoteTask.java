package com.example.andlinker;

import com.codezjx.andlinker.annotation.ClassName;
import com.codezjx.andlinker.annotation.MethodName;

import java.util.List;

/**
 * Created by codezjx on 2018/3/14.<br/>
 */
@ClassName("com.example.andlinker.IRemoteTask")
public interface IRemoteTask {

    @MethodName("remoteCalculate")
    int remoteCalculate(int a, int b);

    @MethodName("getDatas")
    List<ParcelableObj> getDatas();
    
}
