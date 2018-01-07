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
            if (mType == BaseTypeWrapper.TYPE_PARCELABLE) {
                dest.writeString(mParam.getClass().getName());
            } else if (mType == BaseTypeWrapper.TYPE_PARCELABLEARRAY) {
                dest.writeInt(Array.getLength(mParam));
                dest.writeString(mParam.getClass().getComponentType().getName());
            } else if (Utils.isArrayType(mType)) {
                // array type just write length
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
        if (mType == BaseTypeWrapper.TYPE_PARCELABLE) {
            String clsName = in.readString();
            mParam = Utils.createObjFromClassName(clsName);
        } else if (mType == BaseTypeWrapper.TYPE_PARCELABLEARRAY) {
            int length = in.readInt();
            String componentType = in.readString();
            mParam = Utils.createArrayFromComponentType(componentType, length);
        } else if (Utils.isArrayType(mType)) {
            int length = in.readInt();
            ArrayType type = (ArrayType) TypeFactory.getType(mType);
            mParam = type.newInstance(length);
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
