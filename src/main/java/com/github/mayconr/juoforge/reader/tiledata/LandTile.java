package com.github.mayconr.juoforge.reader.tiledata;

import java.util.Set;

public record LandTile(
        Set<TileFlag> flags,
        int textureId,
        String name
) { }
