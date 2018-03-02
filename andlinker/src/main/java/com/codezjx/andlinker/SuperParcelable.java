package com.codezjx.andlinker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable parameter classes with @Out or @Inout annotation must inherit from this class.
 */
public interface SuperParcelable extends Parcelable {

    /**
     * Reads the parcel contents into this object, so we can restore it.
     * @param in The parcel to overwrite this object from.
     */
    void readFromParcel(Parcel in);
    
}
