package dev.emortal.minestom.marathon.animator;

import dev.emortal.minestom.marathon.MarathonGame;
import net.minestom.server.ServerFlag;
import net.minestom.server.collision.Aerodynamics;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.FallingBlockMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SuvatAnimator implements BlockAnimator {
    private static final double HEIGHT = 2.2; // height to aim for
    private static final double GRAVITY = -0.06;

    private @Nullable Point lastValidBlock = null;

    @Override
    public void setBlockAnimated(@NotNull Instance instance, @NotNull Point point, @NotNull Block block, @NotNull Point lastPoint) {
        if (this.lastValidBlock == null) {
            this.lastValidBlock = lastPoint;
        }
        Point realLastPoint = this.lastValidBlock.add(0.5, 0, 0.5);

        //   comment from luis:
        // i have spent hours trying to replicate the old physics behaviour of this class.
        // i cannot, for the life of me, figure out why it isn't the same anymore. the same
        // gravity values are being put in, and the same velocities are being calculated.
        // i have now decided that this is good enough, and that someone else should fix it.
        Entity lastEntity = new Entity(EntityType.FALLING_BLOCK);
        lastEntity.setTag(MarathonGame.MARATHON_ENTITY_TAG, true);
        lastEntity.setAerodynamics(new Aerodynamics(GRAVITY, 0, 0));
        lastEntity.editEntityMeta(FallingBlockMeta.class, meta -> meta.setBlock(block));

        double displacementY = point.y() - realLastPoint.y();
        Point displacementXZ = point.sub(realLastPoint);
        double time = Math.sqrt(-2 * HEIGHT / GRAVITY) + Math.sqrt(2 * (displacementY - HEIGHT) / GRAVITY);
        double velocityY = Math.sqrt(-GRAVITY * HEIGHT);
        Point velocityXZ = displacementXZ.div(time);

        Vec combinedVelocity = Vec.fromPoint(velocityXZ.withY(velocityY));
        lastEntity.setVelocity(combinedVelocity.mul(ServerFlag.SERVER_TICKS_PER_SECOND));

        lastEntity.setInstance(instance, realLastPoint);

        lastEntity.scheduler()
                .buildTask(() -> {
                    lastEntity.remove();
                    instance.setBlock(point, block);
                    this.lastValidBlock = point;
                })
                .delay(TaskSchedule.tick(Math.max((int) Math.ceil(time), 1)))
                .schedule();
    }

    @Override
    public void reset() {
        this.lastValidBlock = null;
    }
}
