package com.github.mayconr.juoforge.reader.view;

import com.github.mayconr.juoforge.reader.tiledata.TileFlag;

import java.util.Set;

public interface StaticTile {

    int id();

    int x();

    int y();

    int z();

    String name();

    int height();

    Set<TileFlag> flags();
}
