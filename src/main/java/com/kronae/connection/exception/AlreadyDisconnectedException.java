package com.kronae.connection.exception;

public class AlreadyDisconnectedException extends RuntimeException {
    public AlreadyDisconnectedException(String s) {
        super(s);
    }
}
