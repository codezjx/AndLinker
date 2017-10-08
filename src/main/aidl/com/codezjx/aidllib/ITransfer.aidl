package com.codezjx.aidllib;

import com.codezjx.aidllib.model.Request;
import com.codezjx.aidllib.model.Response;
import com.codezjx.aidllib.ICallback;

interface ITransfer {
    Response execute(in Request request);
    void register(ICallback callback);
    void unRegister(ICallback callback);
}
