package com.universe.yz.admin.model.bean;

public interface BaseListener {

    void onStart();

    void onError(Throwable errorMsg);
}
