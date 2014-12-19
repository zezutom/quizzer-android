package org.zezutom.capstone.android.api;

public interface ResponseListener<T extends Object> {

    void onSuccess(T data);

    void onError(Exception e);
}
