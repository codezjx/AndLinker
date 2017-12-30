package com.codezjx.linker;

import android.os.Parcel;

/**
 * Created by codezjx on 2017/11/28.<br/>
 */
public interface Type<T> {

    void writeToParcel(Parcel dest, int flags, T val);

    T createFromParcel(Parcel in);
    
    final class EmptyType implements Type<Object> {
        
        @Override
        public void writeToParcel(Parcel dest, int flags, Object val) {
            // Nothing to do
        }

        @Override
        public Object createFromParcel(Parcel in) {
            return null;
        }
    }

    final class ByteType implements Type<Byte> {

        @Override
        public void writeToParcel(Parcel dest, int flags, Byte val) {
            dest.writeByte(val);
        }

        @Override
        public Byte createFromParcel(Parcel in) {
            return in.readByte();
        }
    }
    
    final class ShortType implements Type<Short> {

        @Override
        public void writeToParcel(Parcel dest, int flags, Short val) {
            dest.writeInt(val.intValue());
        }

        @Override
        public Short createFromParcel(Parcel in) {
            return (short) in.readInt();
        }
    }

    final class IntType implements Type<Integer> {

        @Override
        public void writeToParcel(Parcel dest, int flags, Integer val) {
            dest.writeInt(val);
        }

        @Override
        public Integer createFromParcel(Parcel in) {
            return in.readInt();
        }
    }
    
    final class LongType implements Type<Long> {

        @Override
        public void writeToParcel(Parcel dest, int flags, Long val) {
            dest.writeLong(val);
        }

        @Override
        public Long createFromParcel(Parcel in) {
            return in.readLong();
        }
    }
    
    final class FloatType implements Type<Float> {

        @Override
        public void writeToParcel(Parcel dest, int flags, Float val) {
            dest.writeFloat(val);
        }

        @Override
        public Float createFromParcel(Parcel in) {
            return in.readFloat();
        }
    }
    
    final class DoubleType implements Type<Double> {
        
        @Override
        public void writeToParcel(Parcel dest, int flags, Double val) {
            dest.writeDouble(val);
        }

        @Override
        public Double createFromParcel(Parcel in) {
            return in.readDouble();
        }
    }
    
    final class BooleanType implements Type<Boolean> {

        @Override
        public void writeToParcel(Parcel dest, int flags, Boolean val) {
            dest.writeInt(val ? 1 : 0);
        }

        @Override
        public Boolean createFromParcel(Parcel in) {
            return in.readInt() == 1;
        }
    }
    
    final class CharType implements Type<Character> {

        @Override
        public void writeToParcel(Parcel dest, int flags, Character val) {
            dest.writeInt(((int) val));
        }

        @Override
        public Character createFromParcel(Parcel in) {
            return (char) in.readInt();
        }
    }
    
    final class StringType implements Type<String> {

        @Override
        public void writeToParcel(Parcel dest, int flags, String val) {
            dest.writeString(val);
        }

        @Override
        public String createFromParcel(Parcel in) {
            return in.readString();
        }
    }
    
    final class CharSequenceType implements Type<CharSequence> {

        @Override
        public void writeToParcel(Parcel dest, int flags, CharSequence val) {
            dest.writeCharSequence(val);
        }

        @Override
        public CharSequence createFromParcel(Parcel in) {
            return in.readCharSequence();
        }
    }
    
}
