package com.codezjx.andlinker;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;

/**
 * Created by codezjx on 2017/9/14.<br/>
 */
final class Utils {
    
    private static final String TAG = "Utils";
    
    private Utils() {
        // private constructor
    }

    static <T> void validateServiceInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        // Prevent API interfaces from extending other interfaces. This not only avoids a bug in
        // Android (http://b.android.com/58753) but it forces composition of API declarations which is
        // the recommended pattern.
        if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
    }

    static boolean hasUnresolvableType(Type type) {
        if (type instanceof Class<?>) {
            return false;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                if (hasUnresolvableType(typeArgument)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof GenericArrayType) {
            return hasUnresolvableType(((GenericArrayType) type).getGenericComponentType());
        }
        if (type instanceof TypeVariable) {
            return true;
        }
        if (type instanceof WildcardType) {
            return true;
        }
        String className = type == null ? "null" : type.getClass().getName();
        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                + "GenericArrayType, but <" + type + "> is of type " + className);
    }

    static Class<?> getRawType(Type type) {
        checkNotNull(type, "type == null");

        if (type instanceof Class<?>) {
            // Type is a normal class.
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) throw new IllegalArgumentException();
            return (Class<?>) rawType;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // type that's more general than necessary is okay.
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        }

        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                + "GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
    }

    static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    static boolean canOnlyBeInType(Class<?> classType) {
        return isPrimitiveType(classType) || classType == String.class || classType == CharSequence.class;
    }

    static boolean isArrayType(int type) {
        return type == BaseTypeWrapper.TYPE_BYTEARRAY || type == BaseTypeWrapper.TYPE_SHORTARRAY || type == BaseTypeWrapper.TYPE_INTARRAY
                || type == BaseTypeWrapper.TYPE_LONGARRAY || type == BaseTypeWrapper.TYPE_FLOATARRAY || type == BaseTypeWrapper.TYPE_DOUBLEARRAY
                || type == BaseTypeWrapper.TYPE_BOOLEANARRAY || type == BaseTypeWrapper.TYPE_CHARARRAY || type == BaseTypeWrapper.TYPE_STRINGARRAY
                || type == BaseTypeWrapper.TYPE_CHARSEQUENCEARRAY || type == BaseTypeWrapper.TYPE_PARCELABLEARRAY;
    }

    static boolean isPrimitiveType(Class<?> classType) {
        return classType.isPrimitive() || classType == Byte.class || classType == Short.class
                || classType == Integer.class || classType == Long.class || classType == Float.class
                || classType == Double.class || classType == Boolean.class || classType == Character.class;
    }

    static boolean isArrayType(Class<?> classType) {
        return classType == byte[].class || classType == short[].class || classType == int[].class || classType == long[].class
                || classType == float[].class || classType == double[].class || classType == boolean[].class || classType == char[].class
                || classType == String[].class || classType == CharSequence[].class || classType == Parcelable[].class;
    }

    static int getTypeByClass(Class<?> classType) {
        int type;
        if (classType == byte.class || classType == Byte.class) {
            type = BaseTypeWrapper.TYPE_BYTE;
        } else if (classType == short.class || classType == Short.class) {
            type = BaseTypeWrapper.TYPE_SHORT;
        } else if (classType == int.class || classType == Integer.class) {
            type = BaseTypeWrapper.TYPE_INT;
        } else if (classType == long.class || classType == Long.class) {
            type = BaseTypeWrapper.TYPE_LONG;
        } else if (classType == float.class || classType == Float.class) {
            type = BaseTypeWrapper.TYPE_FLOAT;
        } else if (classType == double.class || classType == Double.class) {
            type = BaseTypeWrapper.TYPE_DOUBLE;
        } else if (classType == boolean.class || classType == Boolean.class) {
            type = BaseTypeWrapper.TYPE_BOOLEAN;
        } else if (classType == char.class || classType == Character.class) {
            type = BaseTypeWrapper.TYPE_CHAR;
        } else if (classType == byte[].class) {
            type = BaseTypeWrapper.TYPE_BYTEARRAY;
        } else if (classType == short[].class) {
            type = BaseTypeWrapper.TYPE_SHORTARRAY;
        } else if (classType == int[].class) {
            type = BaseTypeWrapper.TYPE_INTARRAY;
        } else if (classType == long[].class) {
            type = BaseTypeWrapper.TYPE_LONGARRAY;
        } else if (classType == float[].class) {
            type = BaseTypeWrapper.TYPE_FLOATARRAY;
        } else if (classType == double[].class) {
            type = BaseTypeWrapper.TYPE_DOUBLEARRAY;
        } else if (classType == boolean[].class) {
            type = BaseTypeWrapper.TYPE_BOOLEANARRAY;
        } else if (classType == char[].class) {
            type = BaseTypeWrapper.TYPE_CHARARRAY;
        } else if (classType == String.class) {
            type = BaseTypeWrapper.TYPE_STRING;
        } else if (classType == String[].class) {
            type = BaseTypeWrapper.TYPE_STRINGARRAY;
        } else if (classType == CharSequence.class) {
            type = BaseTypeWrapper.TYPE_CHARSEQUENCE;
        } else if (classType == CharSequence[].class) {
            type = BaseTypeWrapper.TYPE_CHARSEQUENCEARRAY;
        } else if (Parcelable.class.isAssignableFrom(classType)) {
            type = BaseTypeWrapper.TYPE_PARCELABLE;
        } else if (Parcelable[].class.isAssignableFrom(classType)) {
            type = BaseTypeWrapper.TYPE_PARCELABLEARRAY;
        } else if (List.class.isAssignableFrom(classType)) {
            type = BaseTypeWrapper.TYPE_LIST;
        } else if (Map.class.isAssignableFrom(classType)) {
            type = BaseTypeWrapper.TYPE_MAP;
        } else {
            type = BaseTypeWrapper.TYPE_EMPTY;
        }
        return type;
    }

    static Object createObjFromClassName(String clsName) {
        Object obj = null;
        try {
            obj = Class.forName(clsName).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    static Object createArrayFromComponentType(String componentType, int length) {
        Object obj = null;
        try {
            Class clsType = Class.forName(componentType);
            obj = Array.newInstance(clsType, length);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    static boolean isStringBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    static Method getMethodReadFromParcel(Class<?> cls) {
        checkNotNull(cls, "Class must not be null.");
        Method method = null;
        try {
            method = cls.getMethod("readFromParcel", Parcel.class);
        } catch (NoSuchMethodException e) {
            Logger.e(TAG, "No public readFromParcel() method in class:" + cls.getName());
        } catch (SecurityException e) {
            Logger.e(TAG, "SecurityException when get readFromParcel() method in class:" + cls.getName());
        }
        return method;
    }
    
}
