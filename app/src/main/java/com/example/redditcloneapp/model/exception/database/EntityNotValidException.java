package com.example.redditcloneapp.model.exception.database;

public class EntityNotValidException extends Exception {
    public EntityNotValidException(Class collection, String message) {
        super("Db exception over: " + collection.getName() + " | Stack trace: " + message);
    }
}
