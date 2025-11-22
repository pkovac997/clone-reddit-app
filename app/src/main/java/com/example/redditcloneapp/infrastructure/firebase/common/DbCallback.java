package com.example.redditcloneapp.infrastructure.firebase.common;

public interface DbCallback<T> {
    void onSuccess(T entity);
    void onError(Exception exception);
}
