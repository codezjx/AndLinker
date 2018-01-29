package com.codezjx.andlinker;

import android.os.Parcel;

/**
 * Created by codezjx on 2017/11/30.<br/>
 */
public class InOutTypeWrapper implements BaseTypeWrapper {

    private int mType;
    private Object mParam;

    InOutTypeWrapper(Object param, Class<?> mParamType) {
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
        type.writeToParcel(dest, flags, mParam);
    }

    @Override
    public void readFromParcel(Parcel in) {
        mType = in.readInt();
        OutType type = (OutType) TypeFactory.getType(mType);
        type.readFromParcel(in, mParam);
    }

    protected InOutTypeWrapper(Parcel in) {
        mType = in.readInt();
        Type type = TypeFactory.getType(mType);
        mParam = type.createFromParcel(in);
    }

    public static final Creator<InOutTypeWrapper> CREATOR = new Creator<InOutTypeWrapper>() {
        @Override
        public InOutTypeWrapper createFromParcel(Parcel source) {
            return new InOutTypeWrapper(source);
        }

        @Override
        public InOutTypeWrapper[] newArray(int size) {
            return new InOutTypeWrapper[size];
        }
    };
    
}
