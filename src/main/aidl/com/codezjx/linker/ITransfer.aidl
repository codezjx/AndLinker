package com.codezjx.linker;

import com.codezjx.linker.model.Request;
import com.codezjx.linker.model.Response;
import com.codezjx.linker.ICallback;

interface ITransfer {
    Response execute(in Request request);
    void register(ICallback callback);
    void unRegister(ICallback callback);
}
