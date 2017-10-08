package com.codezjx.aidllib.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by codezjx on 2017/9/13.<br/>
 */
public class Response implements Parcelable {
    
    private int mStatusCode;
    private String mStatusMessage;
    private Object mResult;

    public Response(int statusCode, String statusMessage, Object result) {
        mStatusCode = statusCode;
        mStatusMessage = statusMessage;
        mResult = result;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStatusCode);
        dest.writeString(this.mStatusMessage);
        dest.writeValue(mResult);
    }

    protected Response(Parcel in) {
        mStatusCode = in.readInt();
        mStatusMessage = in.readString();
        mResult = in.readValue(getClass().getClassLoader());
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel source) {
            return new Response(source);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

    public int getStatusCode() {
        return mStatusCode;
    }

    public String getStatusMessage() {
        return mStatusMessage;
    }

    public Object getResult() {
        return mResult;
    }

    @Override
    public String toString() {
        return "Response{" +
                "mStatusCode=" + mStatusCode +
                ", mStatusMessage='" + mStatusMessage + '\'' +
                ", mResult=" + mResult +
                '}';
    }
}
