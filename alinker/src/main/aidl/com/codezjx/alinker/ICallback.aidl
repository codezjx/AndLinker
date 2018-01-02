package com.codezjx.alinker;

import com.codezjx.alinker.model.Request;
import com.codezjx.alinker.model.Response;

interface ICallback {
    Response callback(in Request request);
}
