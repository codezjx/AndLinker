package com.codezjx.linker;

import com.codezjx.linker.model.Request;

/**
 * Created by codezjx on 2017/11/5.<br/>
 */
final class RequestBuilder {

    private String mTargetClass;
    private String mMethodName;
    private ParameterWrapper[] mParameterWrappers;

    RequestBuilder(String targetClass, String methodName, int argumentCount) {
        mTargetClass = targetClass;
        mMethodName = methodName;
        mParameterWrappers = new ParameterWrapper[argumentCount];
    }

    void applyWrapper(int index, ParameterWrapper wrapper) {
        if (index < 0 || index >= mParameterWrappers.length) {
            throw new IllegalArgumentException("Index out of range.");
        }
        mParameterWrappers[index] = wrapper;
    }

    Request build() {
        return new Request(mTargetClass, mMethodName, mParameterWrappers);
    }

}
