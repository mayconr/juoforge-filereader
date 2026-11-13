package com.github.mayconr.juoforge.reader.art;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public final class ArtReader {

    private static final int LAND_COUNT = 0x4000;
    private static final int INDEX_ENTRY_SIZE = 12;
    private final ArtMetadata[] entries;

    public static ArtReader load(Path mulPath, Path idxPath) {
        try {
            return new ArtReader(mulPath, idxPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public ArtReader(Path mulPath, Path idxPath) throws IOException {
        try (var mul = new RandomAccessFile(mulPath.toFile(), "r");
             var idx = new RandomAccessFile(idxPath.toFile(), "r")) {
            if (idx.length() % INDEX_ENTRY_SIZE != 0) {
                throw new IOException("Incomplete art index entry");
            }
            entries = new ArtMetadata[Math.toIntExact(idx.length() / INDEX_ENTRY_SIZE)];
            loadEntries(mul, idx);
        }
    }

    private void loadEntries(RandomAccessFile mul, RandomAccessFile idx) throws IOException {
        for (int index = 0; index < entries.length; index++) {
            long offset = Integer.toUnsignedLong(readInt(idx));
            int length = readInt(idx);
            int extra = readInt(idx);
            if (offset == 0xFFFFFFFFL || length <= 0) {
                continue;
            }
            if (offset > mul.length() || length > mul.length() - offset) {
                throw new IOException("Art entry outside data file: " + index);
            }
            entries[index] = readMetadata(mul, index, offset, length, extra);
        }
    }

    private ArtMetadata readMetadata(
            RandomAccessFile mul, int index, long offset, int length, int extra
    ) throws IOException {
        if (index < LAND_COUNT) {
            if (length < 2024) {
                throw new IOException("Incomplete land art: " + index);
            }
            return new ArtMetadata(index, index, true, offset, length, extra,
                    OptionalLong.empty(), 44, 44, List.of());
        }
        if (length < 8) {
            throw new IOException("Incomplete static art header: " + index);
        }
        mul.seek(offset);
        long header = Integer.toUnsignedLong(readInt(mul));
        int width = Short.reverseBytes(mul.readShort());
        int height = Short.reverseBytes(mul.readShort());
        if (width <= 0 || height <= 0 || 8L + height * 2L > length) {
            throw new IOException("Invalid static art dimensions: " + index);
        }
        List<Integer> rows = readRowOffsets(mul, height, length, index);
        return new ArtMetadata(index, index - LAND_COUNT, false, offset, length, extra,
                OptionalLong.of(header), width, height, rows);
    }

    private List<Integer> readRowOffsets(
            RandomAccessFile mul, int height, int length, int index
    ) throws IOException {
        var rows = new ArrayList<Integer>(height);
        for (int row = 0; row < height; row++) {
            int offset = Short.toUnsignedInt(Short.reverseBytes(mul.readShort()));
            if (8L + height * 2L + offset * 2L + 4 > length) {
                throw new IOException("Static art row outside entry: " + index);
            }
            rows.add(offset);
        }
        return rows;
    }

    private static int readInt(RandomAccessFile file) throws IOException {
        return Integer.reverseBytes(file.readInt());
    }

    public Optional<ArtMetadata> get(int index) {
        if (index < 0 || index >= entries.length) {
            return Optional.empty();
        }
        return Optional.ofNullable(entries[index]);
    }

    public Optional<ArtMetadata> land(int id) {
        return id < 0 || id >= LAND_COUNT ? Optional.empty() : get(id);
    }

    public Optional<ArtMetadata> item(int id) {
        return id < 0 || id > Integer.MAX_VALUE - LAND_COUNT
                ? Optional.empty() : get(id + LAND_COUNT);
    }

    public List<ArtMetadata> entries() {
        var result = new ArrayList<ArtMetadata>();
        for (ArtMetadata entry : entries) {
            if (entry != null) {
                result.add(entry);
            }
        }
        return List.copyOf(result);
    }
}
