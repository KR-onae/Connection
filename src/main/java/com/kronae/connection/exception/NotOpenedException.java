package com.kronae.connection.exception;

public class NotOpenedException extends RuntimeException {
    public NotOpenedException(Exception exception) {
        super(exception);
    }
}
