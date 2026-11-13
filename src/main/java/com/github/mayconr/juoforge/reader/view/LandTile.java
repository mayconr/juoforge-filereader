package com.github.mayconr.juoforge.reader.view;

import com.github.mayconr.juoforge.reader.tiledata.TileFlag;

import java.util.Set;

public interface LandTile {

    int id();

    int x();

    int y();

    int z();

    String name();

    Set<TileFlag> flags();

}
