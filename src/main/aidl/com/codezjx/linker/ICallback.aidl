package com.codezjx.linker;

import com.codezjx.linker.model.Request;
import com.codezjx.linker.model.Response;

interface ICallback {
    Response callback(in Request request);
}
