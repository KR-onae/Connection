package com.kronae.connection.connection;

import com.kronae.connection.event.SocketEvent;
import com.kronae.connection.exception.NotOpenedException;
import com.kronae.connection.utils.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ServerConnection {
    private final int          port  ;
    private ServerSocket server;
    private final ArrayList<ConnectionListener> connectListeners;
    private final ArrayList<ConnectionListener> disconnectListeners;
    private int setAcceptors = 5;
    private int nowAcceptors = 0;

    public ServerConnection(int port) {
        this.port = port;
        connectListeners = new ArrayList<>();
        disconnectListeners = new ArrayList<>();
    }
    public void open() throws IOException {
        server = new ServerSocket(port);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                while(nowAcceptors < setAcceptors)
                    addAcceptor();
            }
        }, 1, 1);
    }
    public boolean isOpen() {
        if(server == null)
            return false;
        return !server.isClosed();
    }
    private void addAcceptor() throws NotOpenedException {
        if(!isOpen()) throw new NotOpenedException(new SocketException("Cannot add acceptor because the server is not open."));
        if(nowAcceptors >= setAcceptors) return;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                nowAcceptors++;

                Socket clientSocket;
                try {
                    clientSocket = server.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                addAcceptor();

                ConnectedClientConnection client = new ConnectedClientConnection(ServerConnection.this, clientSocket);
                client.setWriteable(true);
                connectListeners.forEach(connectListener -> {
                    try {
                        connectListener.run(client);
                    } catch (Exception e) {
                        disconn(client);
                    }
                });
                nowAcceptors--;
            }
        }, 0);
    }

    private void disconn(ConnectedClientConnection client) {
        client.disconnect();
        disconnectListeners.forEach(listener -> {
            try {
                listener.run(client);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public ServerConnection addListener(SocketEvent event, ConnectionListener listener) {
        if(event == SocketEvent.CONNECT) connectListeners    .add(listener);
        if(event == SocketEvent.ERROR  ) disconnectListeners .add(listener);
        return this;
    }
    public void close() throws IOException {
        server.close();
    }

    /**
     *
     * @param accepts The count of acceptors. Default value is 5
     * @return this
     */
    public ServerConnection setAcceptors(int accepts) {
        this.setAcceptors = accepts;
        if(accepts < 0)
            accepts = 0;
        return this;
    }
}
