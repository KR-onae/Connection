package com.kronae.connection.exception;

public class NotWriteableException extends RuntimeException {
    public NotWriteableException(Exception exception) {
        super(exception);
    }
    public NotWriteableException(String s) {
        super(s);
    }
}
