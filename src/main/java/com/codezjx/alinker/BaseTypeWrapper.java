package com.codezjx.alinker;

/**
 * Created by codezjx on 2017/11/19.<br/>
 */
public interface BaseTypeWrapper extends SuperParcelable {

    int TYPE_EMPTY = 0;
    
    // Primitives
    int TYPE_BYTE    = 1;
    int TYPE_SHORT   = 2;
    int TYPE_INT     = 3;
    int TYPE_LONG    = 4;
    int TYPE_FLOAT   = 5;
    int TYPE_DOUBLE  = 6;
    int TYPE_BOOLEAN = 7;
    int TYPE_CHAR    = 8;
    
    // Primitive Arrays
    int TYPE_BYTEARRAY    = 9;
    int TYPE_SHORTARRAY   = 10;
    int TYPE_INTARRAY     = 11;
    int TYPE_LONGARRAY    = 12;
    int TYPE_FLOATARRAY   = 13;
    int TYPE_DOUBLEARRAY  = 14;
    int TYPE_BOOLEANARRAY = 15;
    int TYPE_CHARARRAY    = 16;

    // Other
    int TYPE_STRING            = 17;
    int TYPE_STRINGARRAY       = 18;
    int TYPE_CHARSEQUENCE      = 19;
    int TYPE_CHARSEQUENCEARRAY = 20;
    int TYPE_PARCELABLE        = 21;
    int TYPE_PARCELABLEARRAY   = 22;
    int TYPE_LIST              = 23;
    int TYPE_MAP               = 24;
    int TYPE_CALLBACK          = 25;

    int getType();

    Object getParam();
}
