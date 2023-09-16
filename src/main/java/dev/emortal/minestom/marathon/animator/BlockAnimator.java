package dev.emortal.minestom.marathon.animator;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public interface BlockAnimator {

    void setBlockAnimated(@NotNull Instance instance, @NotNull Point point, @NotNull Block block, @NotNull Point lastPoint);

    default void destroyBlockAnimated(@NotNull Instance instance, @NotNull Point point, @NotNull Block block) {
        instance.setBlock(point, Block.AIR);
    }

    void reset();
}
