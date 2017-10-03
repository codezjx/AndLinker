package com.codezjx.aidllib;


import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by codezjx on 2017/9/17.<br/>
 */
public interface ParameterHandler<T> {

    void apply(Bundle bundle, T value);

    final class ParamNameHandler<T> implements ParameterHandler<T> {

        String mParamName;
        Class<?> mParamType;

        public ParamNameHandler(String paramName, Class<?> paramType) {
            mParamName = paramName;
            mParamType = paramType;
        }

        @Override
        public void apply(Bundle bundle, T value) {
            Log.d("ParamNameHandler", "ParameterHandler mParamName:" + mParamName + " mParamType:" + mParamType + " value:" + value);
            if (boolean.class == mParamType) {
                bundle.putBoolean(mParamName, (Boolean) value);
            } else if (byte.class == mParamType) {
                bundle.putByte(mParamName, (Byte) value);
            } else if (char.class == mParamType) {
                bundle.putChar(mParamName, (Character) value);
            } else if (double.class == mParamType) {
                bundle.putDouble(mParamName, (Double) value);
            } else if (float.class == mParamType) {
                bundle.putFloat(mParamName, (Float) value);
            } else if (int.class == mParamType) {
                bundle.putInt(mParamName, (Integer) value);
            } else if (long.class == mParamType) {
                bundle.putLong(mParamName, (Long) value);
            } else if (short.class == mParamType) {
                bundle.putShort(mParamName, (Short) value);
            } else if (String.class == mParamType) {
                bundle.putString(mParamName, (String) value);
            } else if (Parcelable.class == mParamType) {
                bundle.putParcelable(mParamName, (Parcelable) value);
            } else {
                throw new IllegalArgumentException("Param type must be parcelable object or primitive.");
            }
        }

    }
}
