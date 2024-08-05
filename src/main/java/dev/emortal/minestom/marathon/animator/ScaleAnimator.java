package dev.emortal.minestom.marathon.animator;

import dev.emortal.minestom.marathon.MarathonGame;
import dev.emortal.minestom.marathon.util.NoPhysicsEntity;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

public final class ScaleAnimator implements BlockAnimator {
    @Override
    public void setBlockAnimated(@NotNull Instance instance, @NotNull Point point, @NotNull Block block, @NotNull Point lastPoint) {
        int effectDuration = 3;

        Entity entity = new NoPhysicsEntity(EntityType.BLOCK_DISPLAY);
        entity.setNoGravity(true);
        entity.setTag(MarathonGame.MARATHON_ENTITY_TAG, true);
        entity.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(Block.AIR);
            meta.setScale(new Vec(0.5));
            meta.setTranslation(new Vec(-0.25, 0.0, -0.25));
            meta.setTransformationInterpolationStartDelta(0);
            meta.setTransformationInterpolationDuration(effectDuration);
        });

        entity.setInstance(instance, point).thenRun(() -> {
            entity.scheduler().buildTask(() -> {
                entity.editEntityMeta(BlockDisplayMeta.class, meta -> {
                    meta.setBlockState(block);
                    meta.setTranslation(new Vec(-0.5, 0.0, -0.5));
                    meta.setScale(new Vec(1));
                });
            }).delay(TaskSchedule.tick(2)).schedule();
        });

        entity.scheduler()
                .buildTask(() -> {
                    instance.setBlock(point, block);
                    entity.scheduleNextTick(Entity::remove);
                })
                .delay(TaskSchedule.tick(effectDuration + 4))
                .schedule();
    }

    @Override
    public void reset() {
    }
}
