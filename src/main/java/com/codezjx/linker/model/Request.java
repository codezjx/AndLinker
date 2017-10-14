package com.codezjx.linker.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by codezjx on 2017/9/13.<br/>
 */
public class Request implements Parcelable {

    private String mTargetClass;
    private String mMethodName;
    private Object[] mArgs;

    public Request(String targetClass, String methodName, Object[] args) {
        mTargetClass = targetClass;
        mMethodName = methodName;
        mArgs = args;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTargetClass);
        dest.writeString(this.mMethodName);
        dest.writeArray(mArgs);
    }

    protected Request(Parcel in) {
        mTargetClass = in.readString();
        mMethodName = in.readString();
        mArgs = in.readArray(getClass().getClassLoader());
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

    public Object[] getArgs() {
        return mArgs;
    }

    @Override
    public String toString() {
        return "Request{" +
                "mTargetClass='" + mTargetClass + '\'' +
                ", mMethodName='" + mMethodName + '\'' +
                ", mArgs=" + Arrays.toString(mArgs) +
                '}';
    }
}
