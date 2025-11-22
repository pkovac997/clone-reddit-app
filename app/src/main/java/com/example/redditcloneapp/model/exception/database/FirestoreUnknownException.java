package com.example.redditcloneapp.model.exception.database;


import com.example.redditcloneapp.model.OperationType;

public class FirestoreUnknownException extends Exception {

    public FirestoreUnknownException(Class collection, OperationType operationType, String message) {
        super();
    }

}
