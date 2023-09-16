package dev.emortal.minestom.marathon.animator;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public final class NoAnimator implements BlockAnimator {

    @Override
    public void setBlockAnimated(@NotNull Instance instance, @NotNull Point point, @NotNull Block block, @NotNull Point lastPoint) {
        instance.setBlock(point, block);
    }

    @Override
    public void reset() {
        // do nothing
    }
}
