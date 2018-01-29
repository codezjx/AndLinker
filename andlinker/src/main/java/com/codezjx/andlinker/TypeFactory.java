package com.codezjx.andlinker;

/**
 * Created by codezjx on 2017/11/28.<br/>
 */
public class TypeFactory {

    private TypeFactory() {
        
    }

    private static Type[] sTypeArr = {
            new Type.EmptyType(),
            new Type.ByteType(),  new Type.ShortType(),  new Type.IntType(),     new Type.LongType(),
            new Type.FloatType(), new Type.DoubleType(), new Type.BooleanType(), new Type.CharType(),
            new ArrayType.ByteArrayType(),  new ArrayType.ShortArrayType(),  new ArrayType.IntArrayType(),     new ArrayType.LongArrayType(),
            new ArrayType.FloatArrayType(), new ArrayType.DoubleArrayType(), new ArrayType.BooleanArrayType(), new ArrayType.CharArrayType(),
            new Type.StringType(), new ArrayType.StringArrayType(), new Type.CharSequenceType(), new ArrayType.CharSequenceArrayType(),
            new OutType.ParcelableType(), new ArrayType.ParcelableArrayType(), new OutType.ListType(), new OutType.MapType()
    };

    static Type getType(int type) {
        return sTypeArr[type];
    }

    static Type getType(Class<?> classType) {
        return sTypeArr[Utils.getTypeByClass(classType)];
    }

}
