package com.codezjx.alinker;

import android.os.Parcel;

import java.lang.reflect.Array;

/**
 * Created by codezjx on 2017/11/30.<br/>
 */
public class OutTypeWrapper implements BaseTypeWrapper {

    private int mType;
    private Object mParam;

    OutTypeWrapper(Object param, Class<?> mParamType) {
        mType = Utils.getTypeByClass(mParamType);
        mParam = param;
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public Object getParam() {
        return mParam;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mType);
        Type type = TypeFactory.getType(mType);
        if (flags == PARCELABLE_WRITE_RETURN_VALUE) {
            type.writeToParcel(dest, flags, mParam);
        } else {
            // array type just write length
            if (Utils.isArrayType(mType)) {
                dest.writeInt(Array.getLength(mParam));
            }
        }
    }

    @Override
    public void readFromParcel(Parcel in) {
        mType = in.readInt();
        OutType type = (OutType) TypeFactory.getType(mType);
        type.readFromParcel(in, mParam);
    }

    protected OutTypeWrapper(Parcel in) {
        mType = in.readInt();
        OutType type = (OutType) TypeFactory.getType(mType);
        if (Utils.isArrayType(mType)) {
            int length = in.readInt();
            mParam = ((ArrayType) type).newInstance(length);
        }
    }

    public static final Creator<OutTypeWrapper> CREATOR = new Creator<OutTypeWrapper>() {
        @Override
        public OutTypeWrapper createFromParcel(Parcel source) {
            return new OutTypeWrapper(source);
        }

        @Override
        public OutTypeWrapper[] newArray(int size) {
            return new OutTypeWrapper[size];
        }
    };
}
