package com.github.mayconr.juoforge.reader.statics;

import com.github.mayconr.juoforge.reader.tiledata.LittleEndianDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StaticsReader implements Closeable {

    private static final int BLOCK_SIZE = 8;

    private static final int STATIC_SIZE = 7;

    private static final int MAP_WIDTH_BLOCKS = 768;

    private static final int MAP_HEIGHT_BLOCKS = 512;

    private static final int BLOCK_COUNT =
            MAP_WIDTH_BLOCKS * MAP_HEIGHT_BLOCKS;

    private final List<Static>[][] blocks;

    private final RandomAccessFile staticsMul;

    public static StaticsReader load(Path staticsMulPath,
                                     Path staticsIdxPath) {
        try {
            return new StaticsReader(staticsMulPath, staticsIdxPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private StaticsReader(
            Path staticsMulPath,
            Path staticsIdxPath
    ) throws IOException {

        this.staticsMul =
                new RandomAccessFile(
                        staticsMulPath.toFile(),
                        "r"
                );

        this.blocks =
                new List[MAP_WIDTH_BLOCKS]
                        [MAP_HEIGHT_BLOCKS];

        try {
            load(staticsIdxPath);
        } catch (IOException | RuntimeException | Error failure) {
            try {
                staticsMul.close();
            } catch (IOException closeFailure) {
                failure.addSuppressed(closeFailure);
            }
            throw failure;
        }
    }

    private void load(
            Path staticsIdxPath
    ) throws IOException {

        try (RandomAccessFile idx =
                     new RandomAccessFile(
                             staticsIdxPath.toFile(),
                             "r"
                     )) {

            for (int block = 0;
                 block < BLOCK_COUNT;
                 block++) {

                int blockX =
                        block / MAP_HEIGHT_BLOCKS;

                int blockY =
                        block % MAP_HEIGHT_BLOCKS;

                int offset =
                        Integer.reverseBytes(
                                idx.readInt()
                        );

                int length =
                        Integer.reverseBytes(
                                idx.readInt()
                        );

                int extra =
                        Integer.reverseBytes(
                                idx.readInt()
                        );

                List<Static> entries =
                        new ArrayList<>();

                blocks[blockX][blockY] =
                        entries;

                if (offset < 0
                        || length <= 0) {

                    continue;
                }

                byte[] data =
                        new byte[length];

                staticsMul.seek(offset);

                staticsMul.readFully(data);

                LittleEndianDataInputStream in =
                        new LittleEndianDataInputStream(
                                new ByteArrayInputStream(
                                        data
                                )
                        );

                int count =
                        length / STATIC_SIZE;

                for (int i = 0;
                     i < count;
                     i++) {

                    int id =
                            Short.toUnsignedInt(
                                    in.readShort()
                            );

                    int dx =
                            in.readUnsignedByte();

                    int dy =
                            in.readUnsignedByte();

                    byte z =
                            in.readByte();

                    int hue =
                            in.readShort();

                    entries.add(
                            new Static(
                                    id,
                                    blockX * BLOCK_SIZE + dx,
                                    blockY * BLOCK_SIZE + dy,
                                    z,
                                    hue
                            )
                    );
                }
            }
        }
    }

    public List<Static> getBlock(
            int blockX,
            int blockY
    ) {

        if (blockX < 0
                || blockX >= MAP_WIDTH_BLOCKS
                || blockY < 0
                || blockY >= MAP_HEIGHT_BLOCKS) {

            return List.of();
        }

        return blocks[blockX][blockY];
    }

    public List<Static> getStatics(
            int x,
            int y
    ) {

        int blockX = x / BLOCK_SIZE;

        int blockY = y / BLOCK_SIZE;

        List<Static> result =
                new ArrayList<>();

        for (Static entry :
                getBlock(blockX, blockY)) {

            if (entry.x() == x
                    && entry.y() == y) {

                result.add(entry);
            }
        }

        return result;
    }

    @Override
    public void close()
            throws IOException {

        staticsMul.close();
    }
}
