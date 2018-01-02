package com.codezjx.alinker;

import android.os.Parcel;

/**
 * Created by codezjx on 2017/11/30.<br/>
 */
public interface OutType<T> extends Type<T> {

    void readFromParcel(Parcel in, T val);
    
}
