package com.codezjx.andlinker;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;

/**
 * Created by codezjx on 2017/11/28.<br/>
 */
interface ArrayType<T> extends OutType<T> {

    T newInstance(int length);
    
    final class ByteArrayType implements ArrayType<byte[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, byte[] val) {
            dest.writeByteArray(val);
        }

        @Override
        public byte[] createFromParcel(Parcel in) {
            return in.createByteArray();
        }

        @Override
        public void readFromParcel(Parcel in, byte[] val) {
            in.readByteArray(val);
        }

        @Override
        public byte[] newInstance(int length) {
            return new byte[length];
        }
    }
    
    final class ShortArrayType implements ArrayType<short[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, short[] val) {
            if (val != null) {
                int N = val.length;
                dest.writeInt(N);
                for (int i = 0; i < N; i++) {
                    dest.writeInt(val[i]);
                }
            } else {
                dest.writeInt(-1);
            }
        }

        @Override
        public short[] createFromParcel(Parcel in) {
            int N = in.readInt();
            if (N >= 0 && N <= (in.dataAvail() >> 2)) {
                short[] val = new short[N];
                for (int i = 0; i < N; i++) {
                    val[i] = (short) in.readInt();
                }
                return val;
            } else {
                return null;
            }
        }

        @Override
        public void readFromParcel(Parcel in, short[] val) {
            int N = in.readInt();
            if (N == val.length) {
                for (int i = 0; i < N; i++) {
                    val[i] = (short) in.readInt();
                }
            } else {
                throw new RuntimeException("bad array lengths");
            }
        }

        @Override
        public short[] newInstance(int length) {
            return new short[length];
        }
    }
    
    final class IntArrayType implements ArrayType<int[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, int[] val) {
            dest.writeIntArray(val);
        }

        @Override
        public int[] createFromParcel(Parcel in) {
            return in.createIntArray();
        }

        @Override
        public void readFromParcel(Parcel in, int[] val) {
            in.readIntArray(val);
        }

        @Override
        public int[] newInstance(int length) {
            return new int[length];
        }
    }
    
    final class LongArrayType implements ArrayType<long[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, long[] val) {
            dest.writeLongArray(val);
        }

        @Override
        public long[] createFromParcel(Parcel in) {
            return in.createLongArray();
        }

        @Override
        public void readFromParcel(Parcel in, long[] val) {
            in.readLongArray(val);
        }

        @Override
        public long[] newInstance(int length) {
            return new long[length];
        }
    }
    
    final class FloatArrayType implements ArrayType<float[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, float[] val) {
            dest.writeFloatArray(val);
        }

        @Override
        public float[] createFromParcel(Parcel in) {
            return in.createFloatArray();
        }

        @Override
        public void readFromParcel(Parcel in, float[] val) {
            in.readFloatArray(val);
        }

        @Override
        public float[] newInstance(int length) {
            return new float[length];
        }
    }
    
    final class DoubleArrayType implements ArrayType<double[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, double[] val) {
            dest.writeDoubleArray(val);
        }

        @Override
        public double[] createFromParcel(Parcel in) {
            return in.createDoubleArray();
        }

        @Override
        public void readFromParcel(Parcel in, double[] val) {
            in.readDoubleArray(val);
        }

        @Override
        public double[] newInstance(int length) {
            return new double[length];
        }
    }
    
    final class BooleanArrayType implements ArrayType<boolean[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, boolean[] val) {
            dest.writeBooleanArray(val);
        }

        @Override
        public boolean[] createFromParcel(Parcel in) {
            return in.createBooleanArray();
        }

        @Override
        public void readFromParcel(Parcel in, boolean[] val) {
            in.readBooleanArray(val);
        }

        @Override
        public boolean[] newInstance(int length) {
            return new boolean[length];
        }
    }
    
    final class CharArrayType implements ArrayType<char[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, char[] val) {
            dest.writeCharArray(val);
        }

        @Override
        public char[] createFromParcel(Parcel in) {
            return in.createCharArray();
        }

        @Override
        public void readFromParcel(Parcel in, char[] val) {
            in.readCharArray(val);
        }

        @Override
        public char[] newInstance(int length) {
            return new char[length];
        }
    }
    
    final class StringArrayType implements ArrayType<String[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, String[] val) {
            dest.writeStringArray(val);
        }

        @Override
        public String[] createFromParcel(Parcel in) {
            return in.createStringArray();
        }

        @Override
        public void readFromParcel(Parcel in, String[] val) {
            in.readStringArray(val);
        }

        @Override
        public String[] newInstance(int length) {
            return new String[length];
        }
    }
    
    final class CharSequenceArrayType implements ArrayType<CharSequence[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, CharSequence[] val) {
            dest.writeCharSequenceArray(val);
        }

        @Override
        public CharSequence[] createFromParcel(Parcel in) {
            return in.readCharSequenceArray();
        }

        @Override
        public void readFromParcel(Parcel in, CharSequence[] val) {
            int N = in.readInt();
            if (N == val.length) {
                for (int i = 0; i < N; i++) {
                    val[i] = in.readCharSequence();
                }
            } else {
                throw new RuntimeException("bad array lengths");
            }
        }

        @Override
        public CharSequence[] newInstance(int length) {
            return new CharSequence[length];
        }
    }

    final class ParcelableArrayType implements ArrayType<Parcelable[]> {

        @Override
        public void writeToParcel(Parcel dest, int flags, Parcelable[] val) {
            dest.writeString(val.getClass().getComponentType().getName());
            dest.writeParcelableArray(val, flags);
        }

        @Override
        public Parcelable[] createFromParcel(Parcel in) {
            String componentType = in.readString();
            Object obj = null;
            try {
                Class cls = Class.forName(componentType);
                obj = createTypedArray(in, cls);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return (Parcelable[]) obj;
        }

        @Override
        public void readFromParcel(Parcel in, Parcelable[] val) {
            // Just read and do nothing, because we don't need component type here
            in.readString();
            int N = in.readInt();
            if (N == val.length) {
                for (int i = 0; i < N; i++) {
                    val[i] = in.readParcelable(getClass().getClassLoader());
                }
            } else {
                throw new RuntimeException("bad array lengths");
            }
        }

        @Override
        public Parcelable[] newInstance(int length) {
            return new Parcelable[length];
        }

        private <T> T[] createTypedArray(Parcel in, Class<T> cls) {
            int N = in.readInt();
            if (N < 0) {
                return null;
            }
            T[] arr = (T[]) Array.newInstance(cls, N);
            for (int i = 0; i < N; i++) {
                arr[i] = in.readParcelable(cls.getClassLoader());
            }
            return arr;
        }
    }
    
}
