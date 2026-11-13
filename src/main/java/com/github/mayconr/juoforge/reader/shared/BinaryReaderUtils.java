package com.github.mayconr.juoforge.reader.shared;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BinaryReaderUtils {
    private BinaryReaderUtils() {
    }

    public static String readString(
            InputStream stream,
            int length
    ) throws IOException {

        byte[] buffer = new byte[length];

        int total = 0;

        while (total < length) {

            int read = stream.read(
                    buffer,
                    total,
                    length - total
            );

            if (read < 0) {
                throw new EOFException();
            }

            total += read;
        }

        return decodeString(buffer);
    }

    public static String readString(
            RandomAccessFile file,
            int length
    ) throws IOException {

        byte[] buffer = new byte[length];

        file.readFully(buffer);

        return decodeString(buffer);
    }

    private static String decodeString(
            byte[] buffer
    ) {

        int end = 0;

        while (end < buffer.length
                && buffer[end] != 0) {

            end++;
        }

        return new String(
                buffer,
                0,
                end,
                StandardCharsets.US_ASCII
        );
    }
}
