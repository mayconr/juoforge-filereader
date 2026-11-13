package com.github.mayconr.juoforge.reader.art;

import java.nio.file.Path;

public final class ArtDimensionsTest {

    private static final int ITEM_ID = 0x1A2C;
    private static final System.Logger LOG = System.getLogger(ArtDimensionsTest.class.getName());

    public static void main(String[] args) {
        Path root = args.length > 0
                ? Path.of(args[0])
                : Path.of("C:\\Program Files (x86)\\Electronic Arts\\Ultima Online Classic");
        var reader = ArtReader.load(root.resolve("art.mul"), root.resolve("artidx.mul"));
        var art = reader.item(ITEM_ID).orElseThrow(
                () -> new AssertionError("Missing art for item 0x1A2C")
        );
        if (art.width() <= 0 || art.height() <= 0) {
            throw new AssertionError("Invalid art dimensions for item 0x1A2C");
        }
        LOG.log(System.Logger.Level.INFO,
                "Item 0x1A2C: width={0}, height={1}", art.width(), art.height());
    }
}
