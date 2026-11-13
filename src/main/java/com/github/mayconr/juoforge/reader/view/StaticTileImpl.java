package com.github.mayconr.juoforge.reader.view;

import com.github.mayconr.juoforge.reader.statics.Static;
import com.github.mayconr.juoforge.reader.tiledata.ItemTileData;
import com.github.mayconr.juoforge.reader.tiledata.TileFlag;

import java.util.Set;

final class StaticTileImpl implements StaticTile {

    private final Static statics;
    private final ItemTileData staticTile;

    public StaticTileImpl(Static statics, ItemTileData staticTile) {
        this.statics = statics;
        this.staticTile = staticTile;
    }

    @Override
    public int id() {
        return statics.id();
    }

    @Override
    public int x() {
        return statics.x();
    }

    @Override
    public int y() {
        return statics.y();
    }

    @Override
    public int z() {
        return statics.z();
    }

    @Override
    public String name() {
        return staticTile.name();
    }

    @Override
    public int height() {
        return staticTile.height();
    }

    @Override
    public Set<TileFlag> flags() {
        return staticTile.flags();
    }

    @Override
    public String toString() {
        return "StaticView[" +
                "id=" + id() +
                ", pos=(" + x() + "," + y() + "," + z() + ")" +
                ", name=" + name() +
                ", height=" + height() +
                ']';
    }
}
