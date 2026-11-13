package com.github.mayconr.juoforge.reader;

import com.github.mayconr.juoforge.reader.view.GameDataProviderFactory;

import java.io.IOException;
import java.nio.file.Path;

public class MainTest {

    public static void main(String[] args) throws IOException {

        /*Path path = Path.of("C:\\Program Files (x86)\\Electronic Arts\\Ultima Online Classic\\tiledata.mul");

        var landTiles = new ArrayList<LandTile>();
        try (TileDataReader reader = TileDataReader.load(path, false)) {




            System.out.println(reader.getStaticTile(2123).flags());
            System.out.println(reader.getStaticTile(2297));
            landTiles.addAll(List.of(reader.landTiles()));
            for (StaticTile staticTile : reader.staticTiles()) {
                //System.out.println(staticTile.count() + staticTile.name());
            }
        }

        Path skillsMul = Path.of("C:\\Program Files (x86)\\Electronic Arts\\Ultima Online Classic\\skills.mul");
        Path skillsIdx = Path.of("C:\\Program Files (x86)\\Electronic Arts\\Ultima Online Classic\\skills.idx");

        try (SkillsLoader loader = SkillsLoader.load(skillsMul, skillsIdx)) {
            System.out.println(loader.get(15));
        }

        try (StaticsReader reader =
                     StaticsReader.load(
                             Path.of("C:\\Program Files (x86)\\Electronic Arts\\Ultima Online Classic\\statics0.mul"),
                             Path.of("C:\\Program Files (x86)\\Electronic Arts\\Ultima Online Classic\\staidx0.mul")
                     )) {

            List<Static> statics =
                    reader.getStatics(4430, 1053);

            for (Static entry : statics) {

                System.out.println(
                        entry.id()
                );
            }
        }

        /*try (SkillsLoader loader = new SkillsLoader(
                skillsMul,
                skillsIdx
        )) {
            SkillDefinition anatomy = loader.get(1);
            System.out.println(anatomy.name());
        }

        try (var map = MapReader.load(Path.of("C:\\Program Files (x86)\\Electronic Arts\\Ultima Online Classic\\map0.mul"))) {
            var mapTile = map.getTile(4312 ,1015);
            var landtile = landTiles.get(mapTile.id());
            System.out.println(landtile);
        }*/

        var dataProvider = GameDataProviderFactory.create(Path.of("C:\\Program Files (x86)\\Electronic Arts\\Ultima Online Classic"), true);



    }

}
