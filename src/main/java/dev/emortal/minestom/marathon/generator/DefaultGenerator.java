package dev.emortal.minestom.marathon.generator;

import net.minestom.server.coordinate.Point;
import net.minestom.server.utils.position.PositionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public final class DefaultGenerator implements Generator {
    public static final @NotNull Generator INSTANCE = new DefaultGenerator();

    @Override
    public @NotNull Point getNextPoint(@NotNull Point current, int targetY, int score) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int y = -1;
        if (targetY == 0) y = random.nextInt(-1, 2);
        if (targetY > current.blockY()) y = 1;

        int z = switch (y) {
            case 1 -> random.nextInt(1, 3);
            case -1 -> random.nextInt(2, 5);
            default -> random.nextInt(1, 4);
        };

        int x = random.nextInt(-3, 4);

        return current.add(x, y, z);
    }
}
