package dev.emortal.minestom.marathon.animator;

import dev.emortal.minestom.marathon.MarathonGame;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public interface BlockAnimator {

    void setBlockAnimated(@NotNull MarathonGame game, @NotNull Point point, @NotNull Block block, @NotNull Point lastPoint);

    default void destroyBlockAnimated(@NotNull MarathonGame game, @NotNull Point point, @NotNull Block block) {
        game.getSpawningInstance().setBlock(point, Block.AIR);
    }

    void reset();

}
