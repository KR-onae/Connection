package com.kronae.connection.forgery;

import java.io.IOException;

@Danger
public class ForgeryClientConnection {
    private final String sourceAddress;
    public ForgeryClientConnection(String clientAddress) {
        this.sourceAddress = clientAddress;
    }
    public ConnectedForgeryClientConnection connect(String serverAddress, int serverPort) throws IOException {
        return new ConnectedForgeryClientConnection(sourceAddress, serverAddress, serverPort);
    }
}
