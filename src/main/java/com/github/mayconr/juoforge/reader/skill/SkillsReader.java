package com.github.mayconr.juoforge.reader.skill;

import com.github.mayconr.juoforge.reader.shared.BinaryReaderUtils;
import com.github.mayconr.juoforge.reader.tiledata.LittleEndianDataInputStream;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SkillsReader implements Closeable {

    private final Skill[] skills;

    private final RandomAccessFile skillsMul;

    private final LittleEndianDataInputStream skillsIdx;

    public static SkillsReader load(Path mulPath, Path idxPath) {
        try {
            return new SkillsReader(mulPath, idxPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public SkillsReader(
            Path mulPath,
            Path idxPath
    ) throws IOException {

        this.skillsMul = new RandomAccessFile(
                mulPath.toFile(),
                "r"
        );

        try {
            this.skillsIdx = new LittleEndianDataInputStream(
                    new BufferedInputStream(Files.newInputStream(idxPath)));
        } catch (IOException | RuntimeException | Error failure) {
            closeAfterFailure(skillsMul, failure);
            throw failure;
        }
        try {
            this.skills = new Skill[calculateSkillCount()];
            load();
        } catch (IOException | RuntimeException | Error failure) {
            closeAfterFailure(skillsIdx, failure);
            closeAfterFailure(skillsMul, failure);
            throw failure;
        }
    }

    private static void closeAfterFailure(Closeable resource, Throwable failure) {
        try {
            resource.close();
        } catch (IOException closeFailure) {
            failure.addSuppressed(closeFailure);
        }
    }

    private int calculateSkillCount() throws IOException {

        long size = skillsIdx.available();

        return (int) (size / 12);
    }

    private void load() throws IOException {

        for (int index = 0; index < skills.length; index++) {

            int offset = skillsIdx.readInt();

            int length = skillsIdx.readInt();

            int extra = skillsIdx.readInt();

            if (offset < 0 || length <= 0) {
                continue;
            }

            if ((long) offset + length > skillsMul.length()) {
                continue;
            }

            skillsMul.seek(offset);

            boolean hasAction =
                    skillsMul.readUnsignedByte() != 0;

            String name = BinaryReaderUtils.readString(skillsMul, length - 1);

            skills[index] = new Skill(
                    index,
                    name,
                    hasAction
            );
        }
    }

    public Skill get(int id) {
        return skills[id];
    }

    public Skill[] skills() {
        return skills;
    }

    @Override
    public void close() throws IOException {

        IOException exception = null;

        try {
            skillsIdx.close();
        } catch (IOException e) {
            exception = e;
        }

        try {
            skillsMul.close();
        } catch (IOException e) {

            if (exception != null) {
                exception.addSuppressed(e);
            } else {
                exception = e;
            }
        }

        if (exception != null) {
            throw exception;
        }
    }
}
