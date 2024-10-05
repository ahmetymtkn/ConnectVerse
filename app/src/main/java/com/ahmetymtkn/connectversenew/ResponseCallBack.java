package com.ahmetymtkn.connectversenew;

public interface ResponseCallBack {
    void onResponse(String response);

    void onError(Throwable throwable);
}
