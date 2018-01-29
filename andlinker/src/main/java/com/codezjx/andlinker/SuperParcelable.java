package com.codezjx.andlinker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by codezjx on 2017/11/2.<br/>
 */
public interface SuperParcelable extends Parcelable {

    /**
     * Read and assign value from a Parcel.
     * @param in The Parcel in which the object should be read.
     */
    void readFromParcel(Parcel in);
    
}
