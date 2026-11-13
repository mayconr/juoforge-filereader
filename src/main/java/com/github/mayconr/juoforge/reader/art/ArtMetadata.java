package com.github.mayconr.juoforge.reader.art;

import java.util.List;
import java.util.OptionalLong;

public record ArtMetadata(
        int index,
        int id,
        boolean land,
        long offset,
        int length,
        int extra,
        OptionalLong header,
        int width,
        int height,
        List<Integer> rowOffsets
) {
    public ArtMetadata {
        rowOffsets = List.copyOf(rowOffsets);
    }
}
