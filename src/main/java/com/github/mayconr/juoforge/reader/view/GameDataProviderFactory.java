package com.github.mayconr.juoforge.reader.view;

import com.github.mayconr.juoforge.reader.art.ArtReader;
import com.github.mayconr.juoforge.reader.map.MapReader;
import com.github.mayconr.juoforge.reader.skill.SkillsReader;
import com.github.mayconr.juoforge.reader.statics.StaticsReader;
import com.github.mayconr.juoforge.reader.tiledata.TileDataReader;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GameDataProviderFactory {

    public static GameDataProvider create(Path root, boolean highSea) {
        try (var executor = Executors.newFixedThreadPool(5)) {
            var map = executor.submit(() -> loadAndClose(() -> MapReader.load(root.resolve("map0.mul"))));
            var statics = executor.submit(() -> loadAndClose(() -> StaticsReader.load(
                    root.resolve("statics0.mul"), root.resolve("staidx0.mul"))));
            var tiles = executor.submit(() -> loadAndClose(
                    () -> TileDataReader.load(root.resolve("tiledata.mul"), !highSea)));
            var skills = executor.submit(() -> loadAndClose(() -> SkillsReader.load(
                    root.resolve("skills.mul"), root.resolve("Skills.idx"))));
            var art = executor.submit(() -> ArtReader.load(root.resolve("art.mul"), root.resolve("artidx.mul")));
            return new GameDataProviderImpl(await(map), await(statics), await(tiles), await(skills), await(art));
        }
    }

    private static <T extends Closeable> T loadAndClose(Callable<T> loader) throws Exception {
        try (T reader = loader.call()) {
            return reader;
        }
    }

    private static <T> T await(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while loading game data", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtime) {
                throw runtime;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            if (cause instanceof IOException io) {
                throw new UncheckedIOException(io);
            }
            throw new IllegalStateException("Failed to load game data", cause);
        }
    }
}
