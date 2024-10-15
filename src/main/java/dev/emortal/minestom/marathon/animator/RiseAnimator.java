package dev.emortal.minestom.marathon.animator;

import dev.emortal.minestom.marathon.MarathonGame;
import dev.emortal.minestom.marathon.util.NoPhysicsEntity;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

public final class RiseAnimator implements BlockAnimator {
    private static final int EFFECT_DURATION = 6;

    @Override
    public void setBlockAnimated(@NotNull Instance instance, @NotNull Point point, @NotNull Block block, @NotNull Point lastPoint) {
        Entity entity = new NoPhysicsEntity(EntityType.BLOCK_DISPLAY);
        entity.setNoGravity(true);
        entity.setTag(MarathonGame.MARATHON_ENTITY_TAG, true);
        entity.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(block);
            meta.setScale(new Vec(1, 0, 1));
            meta.setTransformationInterpolationStartDelta(0);
            meta.setTransformationInterpolationDuration(EFFECT_DURATION);
        });

        entity.setInstance(instance, new Pos(point.blockX(), point.blockY(), point.blockZ())).thenRun(() -> entity.scheduler().buildTask(() -> {
            entity.editEntityMeta(BlockDisplayMeta.class, meta -> {
                meta.setScale(new Vec(1, 1, 1));
            });
        }).delay(TaskSchedule.tick(2)).schedule()); // 2 ticks delay because the client needs to receive the entity first

        entity.scheduler()
                .buildTask(() -> {
                    instance.setBlock(point, block);
                    entity.scheduleNextTick(Entity::remove);
                })
                .delay(TaskSchedule.tick(EFFECT_DURATION + 2))
                .schedule();
    }

    @Override
    public void reset() {
    }
}
