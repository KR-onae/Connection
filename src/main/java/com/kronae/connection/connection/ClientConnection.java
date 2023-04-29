package com.kronae.connection.connection;

import com.kronae.connection.exception.*;
import com.kronae.connection.utils.DataRead;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

public class ClientConnection {
    /* ==================== Private Variables ==================== */
    private final @NotNull String host;
    private final @Range(from=0, to=65536) int port;
    private @Nullable Socket            socket;
    private @Nullable DataOutputStream  outputStream;
    private @Nullable InputStream       inputStream;
    private boolean           connect;

    /* ==================== Constructors ==================== */
    public ClientConnection(@NotNull String host, @Range(from=0, to=65536) int port) {
        this.host    = host;
        this.port    = port;
        this.socket  = null;
        this.connect = false;
        outputStream = null;
        inputStream  = null;

    }
    public ClientConnection(@NotNull Socket socket) {
        this.socket  = socket;
        this.host    = socket.getInetAddress().getHostAddress();
        this.port    = socket.getPort();
        this.connect = socket.isConnected() && (!socket.isClosed()) && socket.isBound();
        outputStream = null;
        inputStream  = null;
    }

    /* ==================== Connect & Disconnect ==================== */

    /**
     * Connect to the server.
     * @param timeout patience (maximum value of waiting) Unit: ms
     * @throws AlreadyConnectException Already connected
     * @throws CannotConnectException IOException while connecting
     * @return Connected connection
     */
    @NotNull
    public ClientConnection connect(@Range(from=1, to=Integer.MAX_VALUE) int timeout) {
        if(connect)
            throw new AlreadyConnectException("Already connect.");

        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port), timeout);
        } catch (IOException e) {
            connect = false;
            throw new CannotConnectException(e);
        }
        connect = true;
        return this;
    }

    /**
     * Disconnect. But server can't detect it.
     * @throws CannotDisconnectException Socket not closing after IOException while disconnecting
     * @throws UnknownDisconnectException Socket closed after IOException thrown while disconnecting
     * @throws AlreadyDisconnectedException When not connected.
     */
    public void disconnect() {
        if(!isConnected())
            throw new AlreadyDisconnectedException("Already disconnected");

        Objects.requireNonNull(socket);

        try {
            socket.close();
            if(inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if(outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            if(!socket.isClosed()) {
                throw new CannotDisconnectException(e);
            } else {
                throw new UnknownDisconnectException(e);
            }
        } finally {
            connect = !socket.isClosed();
        }
    }
    /* ==================== setWrite & setRead ==================== */

    /**
     * Set writeable.
     * @param b writeable value
     */
    public void setWriteable(boolean b) {
        if(!isConnected())
            throw new NotConnectedException("NOT Connected.");
        if(socket == null) throw new NotConnectedException("Please connect before set the mods.");
        if(b) {
            try {
                outputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            if(getWriteable()) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {}
            }
            outputStream = null;
        }
    }
    /**
     * Set readable.
     * @param b readable value
     */
    public ClientConnection setReadable(boolean b) {
        if(!isConnected())
            throw new NotConnectedException("NOT Connected.");
        if(b) {
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            if(getWriteable()) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {}
            }
            inputStream = null;
        }
        return this;
    }
    /* ==================== is...? ==================== */

    /**
     * Get is it connected.
     * @return is connected?
     */
    public boolean isConnected() {
        if(socket == null)
            return false;
        return socket.isConnected() && socket.isBound() && (!socket.isClosed());
    }

    /**
     * Get is it writeable.
     * @return writeable
     */
    public boolean getWriteable() {
        if(!isConnected())
            throw new NotConnectedException("NOT Connected.");
        return outputStream != null;
    }
    /**
     * Get is it readable.
     * @return readable
     */
    public boolean getReadable() {
        if(!isConnected())
            throw new NotConnectedException("NOT Connected.");
        return inputStream != null;
    }
    /* ==================== Get ==================== */

    /**
     * Get raw socket.
     * @return socket
     */
    public Socket getRawSocket() {
        if(!isConnected())
            throw new NotConnectedException("NOT Connected.");
        return socket;
    }
    /**
     * Get client address.
     * @return address
     */
    public InetAddress getAddress() {
        if(!isConnected())
            throw new NotConnectedException("NOT Connected.");
        Objects.requireNonNull(socket);
        return socket.getInetAddress();
    }
    /**
     * Get raw InputStream.
     * @return inputStream
     * @throws IOException When socket.getInputStream() throws IOException
     */
    public InputStream getInputStream() throws IOException {
        if(!isConnected())
            throw new NotConnectedException("NOT Connected.");
        Objects.requireNonNull(socket);
        return socket.getInputStream();
    }
    /**
     * Get raw OutputStream.
     * @return outputStream
     * @throws IOException When socket.getOutputStream() throws IOException
     */
    public OutputStream getOutputStream() throws IOException {
        if(!isConnected())
            throw new NotConnectedException("NOT Connected.");
        Objects.requireNonNull(socket);
        return socket.getOutputStream();
    }

    /* ==================== Write on DataOutputStream ==================== */
    public ClientConnection write(int b) throws NotWriteableException, NotConnectedException {
        if(!isConnected()) throw new NotConnectedException("Cannot write.");
        if(!getWriteable()) throw new NotWriteableException("Cannot write.");

        try {
            outputStream.write(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }
    @Nullable
    public ClientConnection write(byte[] bytes) throws NotWriteableException, NotConnectedException {
        if(!isConnected()) throw new NotConnectedException("Cannot write.");
        if(!getWriteable()) throw new NotWriteableException("Cannot write.");

        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }
    @Nullable
    public ClientConnection writeString(String value) throws NotConnectedException, NotWriteableException {
        if(!isConnected()) throw new NotConnectedException("Cannot write.");
        if(!getWriteable()) throw new NotWriteableException("Cannot write.");

        try {
            outputStream.write(value.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }
    /* ==================== Receive response ==================== */
    @Nullable
    public DataRead read() throws IOException {
        if(!isConnected()) throw new NotConnectedException("Cannot read.");
        if(!getReadable()) throw new NotReadableException("Cannot read.");

        Objects.requireNonNull(inputStream);

        int available = inputStream.available();
        if(available <= 0) return null;

        byte[] buffer = new byte[available];
        int size;
        try {
            size = inputStream.read(buffer, 0, buffer.length);
        } catch (SocketException ignored) {
            return null;
        }

        return new DataRead(size, buffer);
    }
    @Nullable
    public DataRead read(int byteSize) throws IOException {
        if(!isConnected()) throw new NotConnectedException("Cannot read.");
        if(!getReadable()) throw new NotReadableException("Cannot read.");

        Objects.requireNonNull(socket);
        Objects.requireNonNull(inputStream);

        byte[] buffer = new byte[byteSize];
        int size;

        try {
            size = inputStream.read(buffer);
        } catch(SocketException exception) {
            if(socket.isConnected()) {
                socket.close();
                socket = null;
            }
            return null;
        }

        return new DataRead(size, buffer);
    }
    public ClientConnection waitToRead(Consumer<DataRead> reader) throws IOException {
        DataRead data = null;
        while(data == null || (!isConnected())) {
            Thread.onSpinWait();
            data = read();
        }
        if(!isConnected())
            return this;
        reader.accept(data);
        return this;
    }
    public DataRead waitToRead() throws IOException {
        DataRead data = null;
        while(data == null) {
            Thread.onSpinWait();
            data = read();
        }
        return data;
    }
    public void whileConnected(@Nullable Consumer<ClientConnection> consumer) {
        if(socket == null)
            throw new NotConnectedException("Ex");
        while(socket != null) {
            if(!isConnected())
                break;
            Thread.onSpinWait();
            if(consumer != null)
                consumer.accept(this);
        }
    }
}
