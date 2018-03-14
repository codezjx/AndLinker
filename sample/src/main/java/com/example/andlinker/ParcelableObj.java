package com.example.andlinker;

import android.os.Parcel;

import com.codezjx.andlinker.SuperParcelable;

/**
 * Created by codezjx on 2018/3/14.<br/>
 */
public class ParcelableObj implements SuperParcelable {
    
    private int mType;
    private float mValue;
    private String mMsg;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeFloat(this.mValue);
        dest.writeString(this.mMsg);
    }

    public ParcelableObj() {
    }

    public ParcelableObj(int type, float value, String msg) {
        mType = type;
        mValue = value;
        mMsg = msg;
    }

    protected ParcelableObj(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<ParcelableObj> CREATOR = new Creator<ParcelableObj>() {
        @Override
        public ParcelableObj createFromParcel(Parcel source) {
            return new ParcelableObj(source);
        }

        @Override
        public ParcelableObj[] newArray(int size) {
            return new ParcelableObj[size];
        }
    };

    @Override
    public void readFromParcel(Parcel in) {
        mType = in.readInt();
        mValue = in.readFloat();
        mMsg = in.readString();
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float value) {
        mValue = value;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    @Override
    public String toString() {
        return "ParcelableObj{" +
                "mType=" + mType +
                ", mValue=" + mValue +
                ", mMsg='" + mMsg + '\'' +
                '}';
    }
}
