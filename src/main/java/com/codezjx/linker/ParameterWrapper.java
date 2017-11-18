package com.codezjx.linker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by codezjx on 2017/11/1.<br/>
 */
public class ParameterWrapper implements SuperParcelable {
    
    // Type copy from Parcel.java
    static final int TYPE_NULL = -1;
    static final int TYPE_STRING = 0;
    static final int TYPE_INTEGER = 1;
    static final int TYPE_MAP = 2;
    static final int TYPE_BUNDLE = 3;
    static final int TYPE_PARCELABLE = 4;
    static final int TYPE_SHORT = 5;
    static final int TYPE_LONG = 6;
    static final int TYPE_FLOAT = 7;
    static final int TYPE_DOUBLE = 8;
    static final int TYPE_BOOLEAN = 9;
    static final int TYPE_CHARSEQUENCE = 10;
    static final int TYPE_LIST  = 11;
    static final int TYPE_SPARSEARRAY = 12;
    static final int TYPE_BYTEARRAY = 13;
    static final int TYPE_STRINGARRAY = 14;
    static final int TYPE_IBINDER = 15;
    static final int TYPE_PARCELABLEARRAY = 16;
    static final int TYPE_OBJECTARRAY = 17;
    static final int TYPE_INTARRAY = 18;
    static final int TYPE_LONGARRAY = 19;
    static final int TYPE_BYTE = 20;
    static final int TYPE_SERIALIZABLE = 21;
    static final int TYPE_SPARSEBOOLEANARRAY = 22;
    static final int TYPE_BOOLEANARRAY = 23;
    static final int TYPE_CHARSEQUENCEARRAY = 24;
    static final int TYPE_PERSISTABLEBUNDLE = 25;
    static final int TYPE_SIZE = 26;
    static final int TYPE_SIZEF = 27;
    static final int TYPE_DOUBLEARRAY = 28;

    static final int DIRECTION_IN = 0;
    static final int DIRECTION_OUT = 1;
    static final int DIRECTION_INOUT = 2;

    private Object mParam;
    private String mParamClass;
    private int mParamType = TYPE_NULL;
    private int mParamDirection = DIRECTION_IN;
    private boolean mIsCallback = false;

    public ParameterWrapper(Object param) {
        mParam = param;
        mParamClass = param.getClass().getName();
    }

    public Object getParam() {
        return mParam;
    }

    public String getParamClass() {
        return mParamClass;
    }

    public void setParamClass(String paramClass) {
        mParamClass = paramClass;
    }

    public int getParamType() {
        return mParamType;
    }

    public void setParamType(int paramType) {
        mParamType = paramType;
    }

    public int getParamDirection() {
        return mParamDirection;
    }

    public void setParamDirection(int paramDirection) {
        mParamDirection = paramDirection;
    }

    public boolean isCallback() {
        return mIsCallback;
    }

    public void setCallback(boolean callback) {
        mIsCallback = callback;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mParamClass);
        dest.writeInt(mParamDirection);
        dest.writeByte(mIsCallback ? (byte) 1 : (byte) 0);
        if (mIsCallback) {
            return;
        }
        if (flags == PARCELABLE_WRITE_RETURN_VALUE) {
            dest.writeValue(mParam);
        } else {
            if (mParamDirection == DIRECTION_IN || mParamDirection == DIRECTION_INOUT) {
                dest.writeValue(mParam);
            }
        }
    }

    @Override
    public void readFromParcel(Parcel in) {
        mParamClass = in.readString();
        mParamDirection = in.readInt();
        mIsCallback = in.readByte() != 0;
        if (mIsCallback) {
            return;
        }
        if (mParamDirection == DIRECTION_OUT || mParamDirection == DIRECTION_INOUT) {
            // TODO mParam.readFromParcel(in)
            mParam = in.readValue(getClass().getClassLoader());
        }
    }

    protected ParameterWrapper(Parcel in) {
        mParamClass = in.readString();
        mParamDirection = in.readInt();
        mIsCallback = in.readByte() != 0;
        if (mIsCallback) {
            return;
        }
        if (mParamDirection == DIRECTION_IN || mParamDirection == DIRECTION_INOUT) {
            mParam = in.readValue(getClass().getClassLoader());
        } else {
            Object obj = null;
            try {
                obj = Class.forName(mParamClass).newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            mParam = obj;
        }
    }

    public static final Parcelable.Creator<ParameterWrapper> CREATOR = new Parcelable.Creator<ParameterWrapper>() {
        @Override
        public ParameterWrapper createFromParcel(Parcel source) {
            return new ParameterWrapper(source);
        }

        @Override
        public ParameterWrapper[] newArray(int size) {
            return new ParameterWrapper[size];
        }
    };
}
