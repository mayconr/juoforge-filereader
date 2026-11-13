package com.github.mayconr.juoforge.reader.view;

import com.github.mayconr.juoforge.reader.art.ArtMetadata;
import com.github.mayconr.juoforge.reader.art.ArtReader;

import com.github.mayconr.juoforge.reader.map.MapReader;
import com.github.mayconr.juoforge.reader.skill.Skill;
import com.github.mayconr.juoforge.reader.skill.SkillsReader;
import com.github.mayconr.juoforge.reader.statics.Static;
import com.github.mayconr.juoforge.reader.statics.StaticsReader;
import com.github.mayconr.juoforge.reader.tiledata.ItemTileData;
import com.github.mayconr.juoforge.reader.tiledata.TileDataReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class GameDataProviderImpl implements GameDataProvider {

    private final ArtReader artReader;
    private final MapReader mapReader;
    private final StaticsReader staticsReader;
    private final TileDataReader tileDataReader;
    private final SkillsReader skillsReader;

    public GameDataProviderImpl(MapReader mapReader, StaticsReader staticsReader, TileDataReader tileDataReader, SkillsReader skillsReader, ArtReader artReader) {
        this.artReader = artReader;
        this.mapReader = mapReader;
        this.staticsReader = staticsReader;
        this.tileDataReader = tileDataReader;
        this.skillsReader = skillsReader;
    }

    @Override
    public Optional<ArtMetadata> itemArt(int itemId) {
        return artReader.item(itemId);
    }

    @Override
    public Optional<ArtMetadata> landArt(int landId) {
        return artReader.land(landId);
    }

    @Override
    public LandTile tileAt(int x, int y) {
        var mapTile = mapReader.getTile(x, y);
        if (mapTile == null) {
            return null;
        }
        var landTile = tileDataReader.getLandTile(mapTile.id());

        return new LandTileImpl(mapTile, landTile);
    }

    @Override
    public List<StaticTile> staticsAt(int x, int y) {
        var statics = staticsReader.getStatics(x, y);
        var staticsView = new ArrayList<StaticTile>();
        for (Static aStatic : statics) {
            staticsView.add(new StaticTileImpl(aStatic, tileDataReader.getItemTileData(aStatic.id())));
        }
        return staticsView;
    }

    @Override
    public Optional<Skill> skillBy(int skillId) {
        return Optional.ofNullable(skillsReader.skills()[skillId]);
    }

    @Override
    public Optional<ItemTileData> itemTileData(int itemId) {
        var tiles = tileDataReader.itemTiles();
        if (itemId < 0 || itemId >= tiles.length) {
            return Optional.empty();
        }
        return Optional.of(tiles[itemId]);
    }
}
