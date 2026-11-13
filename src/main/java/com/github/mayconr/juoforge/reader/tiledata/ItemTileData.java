package com.github.mayconr.juoforge.reader.tiledata;

import java.util.Set;

/** Entrada do tiledata para um grafico de item (secao de statics do tiledata.mul). */
public record ItemTileData(
        Set<TileFlag> flags,
        int weight,
        int layer,
        int count,
        int animId,
        int hue,
        int lightIndex,
        int height,
        String name
) {

    /** O client so trata como pilha graficos com a flag {@link TileFlag#GENERIC}. */
    public boolean isStackable() {
        return flags.contains(TileFlag.GENERIC);
    }
}
