package com.kronae.connection.exception;

import java.net.SocketException;

public class CannotConnectException extends RuntimeException {
    public CannotConnectException(Exception exception) {
        super(exception);
    }
}
