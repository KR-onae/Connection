package com.kronae.connection.utils;

import com.kronae.connection.connection.ConnectedClientConnection;

public interface ConnectionListener {
    void run(ConnectedClientConnection clientSocket) throws Exception;
}
