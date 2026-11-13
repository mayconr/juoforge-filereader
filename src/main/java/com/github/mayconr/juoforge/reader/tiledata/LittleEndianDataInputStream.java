package com.github.mayconr.juoforge.reader.tiledata;

import java.io.*;

public class LittleEndianDataInputStream extends FilterInputStream {

    private long position;

    public LittleEndianDataInputStream(InputStream in) {
        super(in);
    }

    public long position() {
        return position;
    }

    @Override
    public int read() throws IOException {
        int b = super.read();

        if (b >= 0) {
            position++;
        }

        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {

        int r = super.read(b, off, len);

        if (r > 0) {
            position += r;
        }

        return r;
    }

    public short readShort() throws IOException {

        int b1 = read();
        int b2 = read();

        if ((b1 | b2) < 0) {
            throw new EOFException();
        }

        return (short) ((b2 << 8) | b1);
    }

    public int readUnsignedByte() throws IOException {

        int b = read();

        if (b < 0) {
            throw new EOFException();
        }

        return b;
    }

    public int readInt() throws IOException {

        int b1 = read();
        int b2 = read();
        int b3 = read();
        int b4 = read();

        if ((b1 | b2 | b3 | b4) < 0) {
            throw new EOFException();
        }

        return (b4 << 24)
                | (b3 << 16)
                | (b2 << 8)
                | b1;
    }

    public long readLong() throws IOException {

        long b1 = read();
        long b2 = read();
        long b3 = read();
        long b4 = read();
        long b5 = read();
        long b6 = read();
        long b7 = read();
        long b8 = read();

        if ((b1 | b2 | b3 | b4 | b5 | b6 | b7 | b8) < 0) {
            throw new EOFException();
        }

        return (b8 << 56)
                | (b7 << 48)
                | (b6 << 40)
                | (b5 << 32)
                | (b4 << 24)
                | (b3 << 16)
                | (b2 << 8)
                | b1;
    }

    public byte readByte() throws IOException {

        int b = read();

        if (b < 0) {
            throw new EOFException();
        }

        return (byte) b;
    }
}
