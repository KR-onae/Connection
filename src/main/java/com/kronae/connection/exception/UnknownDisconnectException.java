package com.kronae.connection.exception;

import java.io.IOException;

public class UnknownDisconnectException extends RuntimeException {
    public UnknownDisconnectException(IOException e) {
        super(e);
    }
}
