package com.codezjx.alinker;

import com.codezjx.alinker.model.Request;
import com.codezjx.alinker.model.Response;
import com.codezjx.alinker.ICallback;

interface ITransfer {
    Response execute(inout Request request);
    void register(ICallback callback);
    void unRegister(ICallback callback);
}
