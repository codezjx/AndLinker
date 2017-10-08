package com.codezjx.aidllib;

import com.codezjx.aidllib.model.Request;
import com.codezjx.aidllib.model.Response;

interface ICallback {
    Response callback(in Request request);
}
