package com.codezjx.aidllib.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by codezjx on 2017/9/13.<br/>
 */
public class Request implements Parcelable {

    private String mTargetClass;
    private String mMethodName;
    private Bundle mParams;

    public Request(String targetClass, String methodName, Bundle params) {
        mTargetClass = targetClass;
        mMethodName = methodName;
        mParams = params;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTargetClass);
        dest.writeString(this.mMethodName);
        dest.writeBundle(this.mParams);
    }

    protected Request(Parcel in) {
        this.mTargetClass = in.readString();
        this.mMethodName = in.readString();
        this.mParams = in.readBundle();
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel source) {
            return new Request(source);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    public String getTargetClass() {
        return mTargetClass;
    }

    public String getMethodName() {
        return mMethodName;
    }

    public Bundle getParams() {
        return mParams;
    }
}
