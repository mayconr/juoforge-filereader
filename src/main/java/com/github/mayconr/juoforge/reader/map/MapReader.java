package com.github.mayconr.juoforge.reader.map;

import com.github.mayconr.juoforge.reader.tiledata.LittleEndianDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MapReader implements Closeable {

    private static final int BLOCK_SIZE = 8;

    private static final int MAP_WIDTH_BLOCKS = 768;

    private static final int MAP_HEIGHT_BLOCKS = 512;

    private static final int BLOCK_COUNT =
            MAP_WIDTH_BLOCKS * MAP_HEIGHT_BLOCKS;

    private static final int MAP_BLOCK_SIZE =
            4 + (64 * 3);

    private final MapTile[][] tiles;

    private final Path mapPath;

    public static MapReader load(Path mapPath) {
        try {
            return new MapReader(mapPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private MapReader(
            Path mapPath
    ) throws IOException {

        this.mapPath = mapPath;

        this.tiles =
                new MapTile
                        [MAP_WIDTH_BLOCKS * BLOCK_SIZE]
                        [MAP_HEIGHT_BLOCKS * BLOCK_SIZE];

        load();
    }

    private void load() throws IOException {
        int threads = Math.min(4, Runtime.getRuntime().availableProcessors());
        try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
            Future<?>[] futures = new Future<?>[threads];
            int blocksPerThread = BLOCK_COUNT / threads;
            for (int t = 0; t < threads; t++) {
                int start = t * blocksPerThread;
                int end = t == threads - 1 ? BLOCK_COUNT : start + blocksPerThread;
                futures[t] = executor.submit(() -> {
                    loadRange(start, end);
                    return null;
                });
            }
            awaitRanges(futures);
        }
    }

    private void awaitRanges(Future<?>[] futures) throws IOException {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while loading map", e);
            } catch (ExecutionException e) {
                throw new IOException("Failed to load map", e.getCause());
            }
        }
    }

    private void loadRange(
            int start,
            int end
    ) throws IOException {

        try (RandomAccessFile map =
                     new RandomAccessFile(
                             mapPath.toFile(),
                             "r"
                     )) {

            for (int block = start;
                 block < end;
                 block++) {

                int blockX =
                        block / MAP_HEIGHT_BLOCKS;

                int blockY =
                        block % MAP_HEIGHT_BLOCKS;

                long offset =
                        (long) block
                                * MAP_BLOCK_SIZE;

                map.seek(offset);

                byte[] data =
                        new byte[MAP_BLOCK_SIZE];

                map.readFully(data);

                LittleEndianDataInputStream in =
                        new LittleEndianDataInputStream(
                                new ByteArrayInputStream(
                                        data
                                )
                        );

                int header =
                        in.readInt();

                for (int dy = 0;
                     dy < BLOCK_SIZE;
                     dy++) {

                    for (int dx = 0;
                         dx < BLOCK_SIZE;
                         dx++) {

                        int id =
                                Short.toUnsignedInt(
                                        in.readShort()
                                );

                        byte z =
                                in.readByte();

                        int x =
                                blockX * BLOCK_SIZE + dx;

                        int y =
                                blockY * BLOCK_SIZE + dy;

                        tiles[x][y] =
                                new MapTile(
                                        id,
                                        x,
                                        y,
                                        z
                                );
                    }
                }
            }
        }
    }

    public MapTile getTile(
            int x,
            int y
    ) {

        if (x < 0
                || y < 0
                || x >= tiles.length
                || y >= tiles[0].length) {

            return null;
        }

        return tiles[x][y];
    }

    @Override
    public void close() {
    }
}
