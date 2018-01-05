package com.codezjx.alinker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

/**
 * Created by codezjx on 2017/11/30.<br/>
 */
public interface OutType<T> extends Type<T> {

    void readFromParcel(Parcel in, T val);

    final class ParcelableType implements OutType<Parcelable> {

        @Override
        public void writeToParcel(Parcel dest, int flags, Parcelable val) {
            if (flags == Parcelable.PARCELABLE_WRITE_RETURN_VALUE) {
                val.writeToParcel(dest, flags);
            } else {
                dest.writeParcelable(val, flags);
            }
        }

        @Override
        public Parcelable createFromParcel(Parcel in) {
            return in.readParcelable(getClass().getClassLoader());
        }

        @Override
        public void readFromParcel(Parcel in, Parcelable val) {
            if (!(val instanceof SuperParcelable)) {
                throw new IllegalArgumentException("Parcelable parameter with out type must implements interface SuperParcelable.");
            }
            ((SuperParcelable) val).readFromParcel(in);
        }
    }

    final class ListType implements OutType<List> {

        @Override
        public void writeToParcel(Parcel dest, int flags, List val) {
            dest.writeList(val);
        }

        @Override
        public List createFromParcel(Parcel in) {
            return in.readArrayList(getClass().getClassLoader());
        }

        @Override
        public void readFromParcel(Parcel in, List val) {
            in.readList(val, getClass().getClassLoader());
        }
    }

    final class MapType implements OutType<Map> {

        @Override
        public void writeToParcel(Parcel dest, int flags, Map val) {
            dest.writeMap(val);
        }

        @Override
        public Map createFromParcel(Parcel in) {
            return in.readHashMap(getClass().getClassLoader());
        }

        @Override
        public void readFromParcel(Parcel in, Map val) {
            in.readMap(val, getClass().getClassLoader());
        }
    }
}
