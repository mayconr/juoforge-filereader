package com.github.mayconr.juoforge.reader.view;

import com.github.mayconr.juoforge.reader.art.ArtMetadata;

import com.github.mayconr.juoforge.reader.skill.Skill;
import com.github.mayconr.juoforge.reader.tiledata.ItemTileData;

import java.util.List;
import java.util.Optional;

public interface GameDataProvider {

    Optional<ArtMetadata> itemArt(int itemId);

    Optional<ArtMetadata> landArt(int landId);

    LandTile tileAt(int x, int y);

    List<StaticTile> staticsAt(int x, int y);

    Optional<Skill> skillBy(int skillId);

    /** Retorna os dados do tiledata do grafico de item, ou vazio quando o id esta fora do arquivo. */
    Optional<ItemTileData> itemTileData(int itemId);

}
