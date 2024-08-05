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
import org.jetbrains.annotations.Nullable;

public final class PathAnimator implements BlockAnimator {
    private @Nullable Entity lastEntity = null;

    @Override
    public void setBlockAnimated(@NotNull Instance instance, @NotNull Point point, @NotNull Block block, @NotNull Point lastPoint) {
        Pos realLastPoint = Pos.fromPoint(lastPoint.add(0.5, 0, 0.5));
        if (this.lastEntity != null && !this.lastEntity.isRemoved()) {
            realLastPoint = this.lastEntity.getPosition();
        }

        this.lastEntity = new NoPhysicsEntity(EntityType.BLOCK_DISPLAY);
        this.lastEntity.setTag(MarathonGame.MARATHON_ENTITY_TAG, true);
        this.lastEntity.setNoGravity(true);
        this.lastEntity.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(block);
            meta.setTranslation(new Vec(-0.5, 0.0, -0.5));
            meta.setPosRotInterpolationDuration(3);
        });
        Entity finalEntity = this.lastEntity;
        this.lastEntity.setInstance(instance, realLastPoint).thenRun(() -> {
            finalEntity.scheduler().buildTask(() -> {
                finalEntity.teleport(Pos.fromPoint(point));
            }).repeat(TaskSchedule.tick(1)).schedule();
        });

        this.lastEntity.scheduler()
                .buildTask(() -> {
                    instance.setBlock(point, block);
                    finalEntity.scheduleNextTick(Entity::remove);
                })
                .delay(TaskSchedule.tick(13))
                .schedule();
    }

    @Override
    public void reset() {
        this.lastEntity = null;
    }
}
