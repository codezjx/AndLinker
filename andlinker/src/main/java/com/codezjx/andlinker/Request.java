package com.codezjx.andlinker;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by codezjx on 2017/9/13.<br/>
 */
final class Request implements SuperParcelable {

    private String mTargetClass;
    private String mMethodName;
    private BaseTypeWrapper[] mArgsWrapper;
    // This field use for client slide only
    private boolean mOneWay = false;

    Request(String targetClass, String methodName, BaseTypeWrapper[] argsWrapper) {
        this(targetClass, methodName, argsWrapper, false);
    }

    Request(String targetClass, String methodName, BaseTypeWrapper[] argsWrapper, boolean oneWay) {
        mTargetClass = targetClass;
        mMethodName = methodName;
        mArgsWrapper = argsWrapper;
        mOneWay = oneWay;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTargetClass);
        dest.writeString(mMethodName);
        if (flags == PARCELABLE_WRITE_RETURN_VALUE) {
            writeParcelableArrayToParcel(dest, mArgsWrapper, flags);
        } else {
            dest.writeParcelableArray(mArgsWrapper, flags);
        }
    }

    @Override
    public void readFromParcel(Parcel in) {
        mTargetClass = in.readString();
        mMethodName = in.readString();
        readParcelableArrayFromParcel(in, mArgsWrapper);
    }

    private Request(Parcel in) {
        mTargetClass = in.readString();
        mMethodName = in.readString();
        mArgsWrapper = readParcelableArray(getClass().getClassLoader(), BaseTypeWrapper.class, in);
    }

    private <T extends BaseTypeWrapper> void writeParcelableArrayToParcel(Parcel dest, T[] value, int parcelableFlags) {
        if (value != null) {
            int N = value.length;
            dest.writeInt(N);
            for (int i = 0; i < N; i++) {
                value[i].writeToParcel(dest, parcelableFlags);
            }
        } else {
            dest.writeInt(-1);
        }
    }

    private <T extends BaseTypeWrapper> void readParcelableArrayFromParcel(Parcel in, T[] value) {
        int N = in.readInt();
        if (N < 0) {
            return;
        }
        for (int i = 0; i < N; i++) {
            value[i].readFromParcel(in);
        }
    }

    /**
     * Code from {@link Parcel}.readParcelableArray(ClassLoader loader, Class<T> clazz), it's a hide method.
     */
    @SuppressWarnings("unchecked")
    private <T extends Parcelable> T[] readParcelableArray(ClassLoader loader, Class<T> clazz, Parcel in) {
        int N = in.readInt();
        if (N < 0) {
            return null;
        }
        T[] p = (T[]) Array.newInstance(clazz, N);
        for (int i = 0; i < N; i++) {
            p[i] = in.readParcelable(loader);
        }
        return p;
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

    String getTargetClass() {
        return mTargetClass;
    }

    String getMethodName() {
        return mMethodName;
    }

    boolean isOneWay() {
        return mOneWay;
    }

    BaseTypeWrapper[] getArgsWrapper() {
        return mArgsWrapper;
    }

    @Override
    public String toString() {
        return "Request{" +
                "mTargetClass='" + mTargetClass + '\'' +
                ", mMethodName='" + mMethodName + '\'' +
                ", mArgsWrapper=" + Arrays.toString(mArgsWrapper) +
                '}';
    }
}
