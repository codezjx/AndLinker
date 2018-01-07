package com.codezjx.alinker;

import android.os.Parcel;

/**
 * Created by codezjx on 2017/11/19.<br/>
 */
public class InTypeWrapper implements BaseTypeWrapper {
    
    private int mType;
    private Object mParam;

    public InTypeWrapper(Object param, Class<?> mParamType) {
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
        // Return if write return value, because in type won't readFromParcel
        if (flags == PARCELABLE_WRITE_RETURN_VALUE) {
            return;
        }
        dest.writeInt(mType);
        if (mType == BaseTypeWrapper.TYPE_PARCELABLEARRAY) {
            dest.writeString(mParam.getClass().getComponentType().getName());
        }
        Type type = TypeFactory.getType(mType);
        type.writeToParcel(dest, flags, mParam);
    }

    @Override
    public void readFromParcel(Parcel in) {
        // Nothing to do with in type
    }

    protected InTypeWrapper(Parcel in) {
        mType = in.readInt();
        Type type = TypeFactory.getType(mType);
        if (mType == BaseTypeWrapper.TYPE_PARCELABLEARRAY) {
            String componentType = in.readString();
            mParam = ((ArrayType.ParcelableArrayType) type).createFromComponentType(in, componentType);
        } else {
            mParam = type.createFromParcel(in);
        }
    }

    public static final Creator<InTypeWrapper> CREATOR = new Creator<InTypeWrapper>() {
        @Override
        public InTypeWrapper createFromParcel(Parcel source) {
            return new InTypeWrapper(source);
        }

        @Override
        public InTypeWrapper[] newArray(int size) {
            return new InTypeWrapper[size];
        }
    };
    
}
