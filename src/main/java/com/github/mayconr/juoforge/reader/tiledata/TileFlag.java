package com.github.mayconr.juoforge.reader.tiledata;

import java.util.EnumSet;
import java.util.Set;

public enum TileFlag {

    BACKGROUND(0x00000001L),
    WEAPON(0x00000002L),
    TRANSPARENT(0x00000004L),
    TRANSLUCENT(0x00000008L),
    WALL(0x00000010L),
    DAMAGING(0x00000020L),
    IMPASSABLE(0x00000040L),
    WET(0x00000080L),
    UNKNOWN_1(0x00000100L),
    SURFACE(0x00000200L),
    BRIDGE(0x00000400L),
    /** Marca itens empilhaveis; o client so trata como pilha graficos com esta flag. */
    GENERIC(0x00000800L),
    WINDOW(0x00001000L),
    NO_SHOOT(0x00002000L),
    ARTICLE_A(0x00004000L),
    ARTICLE_AN(0x00008000L),
    INTERNAL(0x00010000L),
    FOLIAGE(0x00020000L),
    PARTIAL_HUE(0x00040000L),
    NO_HOUSE(0x00080000L),
    MAP(0x00100000L),
    CONTAINER(0x00200000L),
    WEARABLE(0x00400000L),
    LIGHT_SOURCE(0x00800000L),
    ANIMATION(0x01000000L),
    NO_DIAGONAL(0x02000000L),
    UNKNOWN_2(0x04000000L),
    ARMOR(0x08000000L),
    ROOF(0x10000000L),
    DOOR(0x20000000L),
    STAIR_BACK(0x40000000L),
    STAIR_RIGHT(0x80000000L),
    ALPHA_BLEND(0x0100000000L),
    USE_NEW_ART(0x0200000000L),
    ART_USED(0x0400000000L),
    NO_SHADOW(0x1000000000L),
    PIXEL_BLEED(0x2000000000L),
    PLAY_ANIM_ONCE(0x4000000000L),
    MULTI_MOVABLE(0x10000000000L);

    private final long mask;

    TileFlag(long mask) {
        this.mask = mask;
    }

    public long mask() {
        return mask;
    }

    public boolean isSet(long flags) {
        return (flags & mask) != 0;
    }

    public static Set<TileFlag> from(long flags) {

        EnumSet<TileFlag> result = EnumSet.noneOf(TileFlag.class);

        for (TileFlag flag : values()) {
            if (flag.isSet(flags)) {
                result.add(flag);
            }
        }

        return result;
    }
}
