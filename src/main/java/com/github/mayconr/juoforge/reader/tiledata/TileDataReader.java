package com.github.mayconr.juoforge.reader.tiledata;

import com.github.mayconr.juoforge.reader.shared.BinaryReaderUtils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TileDataReader implements Closeable {

    private static final int LAND_GROUPS = 512;
    private static final int TILES_PER_GROUP = 32;

    private final LittleEndianDataInputStream in;

    private final boolean oldFormat;

    private final LandTile[] landTiles;

    private final ItemTileData[] itemTiles;

    public static TileDataReader load(Path path, boolean oldFormat) {
        try {
            return new TileDataReader(path, oldFormat);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public TileDataReader(Path path, boolean oldFormat) throws IOException {
        this.in = new LittleEndianDataInputStream(
                new BufferedInputStream(Files.newInputStream(path))
        );

        this.oldFormat = oldFormat;

        try {
            this.landTiles = loadLandTiles();
            this.itemTiles = loadItemTiles(path);
        } catch (IOException | RuntimeException | Error failure) {
            try {
                in.close();
            } catch (IOException closeFailure) {
                failure.addSuppressed(closeFailure);
            }
            throw failure;
        }
    }

    private LandTile[] loadLandTiles() throws IOException {

        LandTile[] tiles = new LandTile[LAND_GROUPS * TILES_PER_GROUP];

        int index = 0;

        for (int group = 0; group < LAND_GROUPS; group++) {

            // header
            in.readInt();

            for (int i = 0; i < TILES_PER_GROUP; i++) {

                long flags = oldFormat
                        ? Integer.toUnsignedLong(in.readInt())
                        : in.readLong();

                int textureId = Short.toUnsignedInt(in.readShort());

                String name = BinaryReaderUtils.readString(in, 20);

                tiles[index++] = new LandTile(
                        TileFlag.from(flags),
                        textureId,
                        name
                );
            }
        }

        return tiles;
    }

    private ItemTileData[] loadItemTiles(Path path) throws IOException {

        long fileSize = Files.size(path);

        long remaining = fileSize - in.position();

        int recordSize = oldFormat
                ? 37
                : 41;

        int groupSize = 4 + (recordSize * 32);

        int groupCount = (int) (remaining / groupSize);

        ItemTileData[] tiles = new ItemTileData[groupCount * 32];

        int index = 0;

        for (int group = 0; group < groupCount; group++) {

            // header
            in.readInt();

            for (int i = 0; i < 32; i++) {

                long flags = oldFormat
                        ? Integer.toUnsignedLong(in.readInt())
                        : in.readLong();

                int weight = in.readUnsignedByte();

                int layer = in.readUnsignedByte();

                int count = in.readInt();

                int animId = Short.toUnsignedInt(in.readShort());

                int hue = Short.toUnsignedInt(in.readShort());

                int lightIndex = Short.toUnsignedInt(in.readShort());

                int height = in.readUnsignedByte();

                String name = BinaryReaderUtils.readString(in, 20);

                tiles[index++] = new ItemTileData(
                        TileFlag.from(flags),
                        weight,
                        layer,
                        count,
                        animId,
                        hue,
                        lightIndex,
                        height,
                        name
                );
            }
        }

        return tiles;
    }

    private String readFixedString(int size) throws IOException {

        byte[] buffer = new byte[size];

        int total = 0;

        while (total < size) {

            int read = in.read(buffer, total, size - total);

            if (read == -1) {
                throw new EOFException();
            }

            total += read;
        }

        int len = 0;

        while (len < buffer.length && buffer[len] != 0) {
            len++;
        }

        return new String(buffer, 0, len, StandardCharsets.UTF_8);
    }

    public LandTile getLandTile(int id) {
        return landTiles[id];
    }

    public ItemTileData getItemTileData(int id) {
        return itemTiles[id];
    }

    public LandTile[] landTiles() {
        return landTiles;
    }

    public ItemTileData[] itemTiles() {
        return itemTiles;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
