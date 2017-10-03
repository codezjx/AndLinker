package com.codezjx.aidllib;

import com.codezjx.aidllib.model.Request;
import com.codezjx.aidllib.model.Response;

interface ITransfer {
    Response execute(in Request request);
}
