package com.codezjx.andlinker;

/**
 * Created by codezjx on 2017/11/5.<br/>
 */
final class RequestBuilder {

    private String mTargetClass;
    private String mMethodName;
    private BaseTypeWrapper[] mParameterWrappers;
    private boolean mOneWay;

    RequestBuilder(String targetClass, String methodName, int argumentCount, boolean oneWay) {
        mTargetClass = targetClass;
        mMethodName = methodName;
        mParameterWrappers = new BaseTypeWrapper[argumentCount];
        mOneWay = oneWay;
    }

    void applyWrapper(int index, BaseTypeWrapper wrapper) {
        if (index < 0 || index >= mParameterWrappers.length) {
            throw new IllegalArgumentException("Index out of range.");
        }
        mParameterWrappers[index] = wrapper;
    }

    Request build() {
        return new Request(mTargetClass, mMethodName, mParameterWrappers, mOneWay);
    }

}
