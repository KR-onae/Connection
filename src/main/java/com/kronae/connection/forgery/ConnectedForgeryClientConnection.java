package com.kronae.connection.forgery;

import java.io.IOException;
import java.net.*;

@Danger
public class ConnectedForgeryClientConnection {
    private final DatagramSocket client;
    private final String serverAddress;
    private final int serverPort;

    public ConnectedForgeryClientConnection(String sourceAddress, String serverAddress, int serverPort) throws IOException {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.client = new DatagramSocket();

        InetAddress source = InetAddress.getByName(sourceAddress);
        client.send(new DatagramPacket(new byte[0], 0, source, 0));

        client.connect(InetAddress.getByName(serverAddress), serverPort);
    }
    public ConnectedForgeryClientConnection send(byte[] buf) throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(serverAddress), serverPort);
        client.send(packet);
        return this;
    }
    public void close() {
        client.close();
    }
}
