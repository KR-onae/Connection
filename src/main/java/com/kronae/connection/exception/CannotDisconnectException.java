package com.kronae.connection.exception;

import java.io.IOException;

public class CannotDisconnectException extends RuntimeException {
    public CannotDisconnectException(IOException e) {
    }
}
