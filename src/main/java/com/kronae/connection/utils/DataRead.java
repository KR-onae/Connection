package com.kronae.connection.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DataRead {
    private final byte[] bytes;
    private final int size;
    public DataRead(int size, byte[] bytes) {
        this.size = size;
        this.bytes = bytes;
    }
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return Arrays.toString(bytes);
    }
    @Contract(value = "_ -> new", pure = true)
    public @NotNull String getAsString(Charset charset) {
        return new String(bytes, charset);
    }
    @Contract(value = " -> new", pure = true)
    public @NotNull String getAsString() {
        return new String(bytes, StandardCharsets.UTF_8);
    }
    public int getSize() {
        return size;
    }
    public byte[] getBytes() {
        return bytes;
    }
}
