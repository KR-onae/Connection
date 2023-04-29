package com.kronae.connection.exception;

public class NotConnectedException extends RuntimeException {
    public NotConnectedException(Exception exception) {
        super(exception);
    }
    public NotConnectedException(String exception) {
        super(exception);
    }
}
