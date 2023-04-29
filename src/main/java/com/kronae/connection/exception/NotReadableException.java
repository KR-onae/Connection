package com.kronae.connection.exception;

import java.net.SocketException;

public class NotReadableException extends RuntimeException {
    public NotReadableException(Exception exception) {
        super(exception);
    }
    public NotReadableException(String exception) {
        super(exception);
    }
}
