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

import java.util.function.Supplier;

public final class PathAnimator implements BlockAnimator {
    private @Nullable Entity lastEntity = null;

    @Override
    public void setBlockAnimated(@NotNull Instance instance, @NotNull Point point, @NotNull Block block, @NotNull Point lastPoint) {
        Pos realLastPoint;
        if (this.lastEntity != null && !this.lastEntity.isRemoved()) {
            realLastPoint = this.lastEntity.getPosition();
        } else {
            realLastPoint = lastPoint.add(0.5, 0, 0.5).asPos();
        }

        this.lastEntity = new NoPhysicsEntity(EntityType.BLOCK_DISPLAY);
        this.lastEntity.setTag(MarathonGame.MARATHON_ENTITY_TAG, true);
        this.lastEntity.setNoGravity(true);
        this.lastEntity.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(block);
            meta.setTranslation(new Vec(-0.5, 0.0, -0.5));
            meta.setPosRotInterpolationDuration(2);
        });
        Entity finalEntity = this.lastEntity;

        this.lastEntity.setInstance(instance, realLastPoint).thenRun(() ->
                finalEntity.scheduler().submitTask(new Supplier<>() {
                    int i = 0;
                    final int animationTime = 13;

                    @Override
                    public TaskSchedule get() {
                        if (i == animationTime) {
                            i++;
                            instance.setBlock(point, block);
                            return TaskSchedule.tick(1);
                        }
                        if (i == animationTime + 1) {
                            i++;
                            finalEntity.remove();
                            return TaskSchedule.stop();
                        }
                        if (finalEntity.isRemoved()) return TaskSchedule.stop();

                        double frac = (double)i++ / animationTime;

                        Vec animPoint = lerpVec(realLastPoint, point, easeOutExpo(frac));
                        finalEntity.teleport(animPoint.asPos());
                        return TaskSchedule.tick(1);
                    }
                }));
    }

    @Override
    public void reset() {
        this.lastEntity = null;
    }

    private static double easeOutExpo(double x) {
        return 1 - Math.pow(2, -10 * x);
    }

    private static Vec lerpVec(Point a, Point b, double f) {
        return new Vec(
                lerp(a.x(), b.x(), f),
                lerp(a.y(), b.y(), f),
                lerp(a.z(), b.z(), f)
        );
    }

    private static double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }

}
