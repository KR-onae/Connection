package com.kronae.connection.connection;

import com.kronae.connection.forgery.Danger;
import org.jetbrains.annotations.Nullable;

import java.net.Socket;
import java.util.function.Consumer;

public class ConnectedClientConnection extends ClientConnection {
    private final ServerConnection server;
    public ConnectedClientConnection(ServerConnection server, Socket socket) {
        super(socket);
        this.server = server;
    }
    /**
     * Stop using this method! It always will return true!
     * But... Do you want to detect is it disconnected?
     * Hmm, then..! You have to communication!
     * @return IS CONNECTED : ALWAYS TRUE
     */
    @Override @Deprecated @Danger
    public boolean isConnected() {
        return super.isConnected();
    }
    /**
     * Stop using this method! It will NOT END!
     * But... Do you want to detect is it disconnected?
     * Hmm, then..! You have to communication!
     */
    @Override @Deprecated @Danger
    public void whileConnected(@Nullable Consumer<ClientConnection> consumer) {
        super.whileConnected(consumer);
    }
    /**
     * Get server.
     * @return server
     */
    public ServerConnection getServer() {
        return server;
    }
}
