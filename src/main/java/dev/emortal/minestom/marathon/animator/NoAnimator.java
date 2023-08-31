package dev.emortal.minestom.marathon.animator;

import dev.emortal.minestom.marathon.MarathonGame;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public final class NoAnimator implements BlockAnimator {
    public static final @NotNull NoAnimator INSTANCE = new NoAnimator();

    @Override
    public void setBlockAnimated(@NotNull MarathonGame game, @NotNull Point point, @NotNull Block block, @NotNull Point lastPoint) {
        game.getSpawningInstance().setBlock(point, block);
    }

    @Override
    public void reset() {

    }
}
