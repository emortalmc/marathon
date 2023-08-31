package dev.emortal.minestom.marathon.generator;

import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.NotNull;

public interface Generator {
    @NotNull Point getNextPoint(@NotNull Point current, int targetY, int score);
}
