package com.codezjx.alinker;

import android.os.Parcel;

/**
 * Created by codezjx on 2017/11/18.<br/>
 */
public class CallbackTypeWrapper implements BaseTypeWrapper {

    private String mClassName;

    public CallbackTypeWrapper(String className) {
        mClassName = className;
    }

    public String getClassName() {
        return mClassName;
    }

    @Override
    public int getType() {
        return TYPE_CALLBACK;
    }

    @Override
    public Object getParam() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mClassName);
    }

    @Override
    public void readFromParcel(Parcel in) {
        mClassName = in.readString();
    }

    protected CallbackTypeWrapper(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<CallbackTypeWrapper> CREATOR = new Creator<CallbackTypeWrapper>() {
        @Override
        public CallbackTypeWrapper createFromParcel(Parcel source) {
            return new CallbackTypeWrapper(source);
        }

        @Override
        public CallbackTypeWrapper[] newArray(int size) {
            return new CallbackTypeWrapper[size];
        }
    };
}
