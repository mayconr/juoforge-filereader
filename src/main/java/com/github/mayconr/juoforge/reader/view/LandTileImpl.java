package com.github.mayconr.juoforge.reader.view;

import com.github.mayconr.juoforge.reader.map.MapTile;
import com.github.mayconr.juoforge.reader.tiledata.TileFlag;

import java.util.Set;

final class LandTileImpl implements LandTile {

    private final MapTile mapTile;
    private final com.github.mayconr.juoforge.reader.tiledata.LandTile landTile;

    LandTileImpl(
            MapTile mapTile,
            com.github.mayconr.juoforge.reader.tiledata.LandTile landTile
    ) {
        this.mapTile = mapTile;
        this.landTile = landTile;
    }

    @Override
    public int id() {
        return mapTile.id();
    }

    @Override
    public int x() {
        return mapTile.x();
    }

    @Override
    public int y() {
        return mapTile.y();
    }

    @Override
    public int z() {
        return mapTile.z();
    }

    @Override
    public String name() {
        return landTile.name();
    }

    @Override
    public Set<TileFlag> flags() {
        return landTile.flags();
    }

    @Override
    public String toString() {
        return "TileView{" +
                "id=" + id() +
                ", x=" + x() +
                ", y=" + y() +
                ", z=" + z() +
                ", name='" + name() + '\'' +
                ", flags=" + flags() +
                '}';
    }
}
