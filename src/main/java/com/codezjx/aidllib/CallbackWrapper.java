package com.codezjx.aidllib;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by codezjx on 2017/10/8.<br/>
 */
public class CallbackWrapper implements Parcelable {
    
    private String mClassName;

    public CallbackWrapper(String className) {
        mClassName = className;
    }

    public String getClassName() {
        return mClassName;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mClassName);
    }

    protected CallbackWrapper(Parcel in) {
        this.mClassName = in.readString();
    }

    public static final Creator<CallbackWrapper> CREATOR = new Creator<CallbackWrapper>() {
        @Override
        public CallbackWrapper createFromParcel(Parcel source) {
            return new CallbackWrapper(source);
        }

        @Override
        public CallbackWrapper[] newArray(int size) {
            return new CallbackWrapper[size];
        }
    };
}
