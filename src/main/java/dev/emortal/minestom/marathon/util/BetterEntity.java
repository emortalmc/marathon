package dev.emortal.minestom.marathon.util;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntitySpawnType;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Chunk;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;

public class BetterEntity extends Entity {

    public boolean ticking = true;
    public boolean hasDrag = true;
    public boolean hasGravityDrag = true;

    public BetterEntity(@NotNull EntityType entityType) {
        super(entityType);
    }

    @Override
    protected void updateVelocity(boolean wasOnGround, boolean flying, Pos positionBeforeMove, Vec newVelocity) {
        EntitySpawnType type = entityType.registry().spawnType();
        final double airDrag = type == EntitySpawnType.LIVING || type == EntitySpawnType.PLAYER ? 0.91 : 0.98;
        double drag;
        if (wasOnGround) {
            final Chunk chunk = ChunkUtils.retrieve(instance, currentChunk, position);
            synchronized (chunk) {
                drag = chunk.getBlock(positionBeforeMove.sub(0, 0.5000001, 0)).registry().friction() * airDrag;
            }
        } else {
            drag = airDrag;
        }

        double gravity = flying ? 0 : gravityAcceleration;
        double gravityDrag;

        if (!hasGravityDrag) {
            gravityDrag = 1.0;
        } else {
            gravityDrag = flying ? 0.6 : (1 - gravityDragPerTick);
        }
        if (!hasDrag) drag = 1.0;

        double finalDrag = drag;
        this.velocity = newVelocity
                // Apply gravity and drag
                .apply((x, y, z) -> new Vec(
                        x * finalDrag,
                        !hasNoGravity() ? (y - gravity) * gravityDrag : y,
                        z * finalDrag
                ))
                // Convert from block/tick to block/sec
                .mul(MinecraftServer.TICK_PER_SECOND)
                // Prevent infinitely decreasing velocity
                .apply(Vec.Operator.EPSILON);
    }

    @Override
    public void tick(long time) {
        if (this.ticking) super.tick(time);
    }

    public void setDrag(boolean drag) {
        this.hasDrag = drag;
    }

    public boolean hasDrag(boolean drag) {
        return this.hasDrag;
    }

    public void setGravityDrag(boolean drag) {
        this.hasGravityDrag = drag;
    }

    public boolean hasGravityDrag(boolean drag) {
        return this.hasGravityDrag;
    }

    public void setPhysics(boolean physics) {
        this.hasPhysics = physics;
    }

    public boolean hasPhysics() {
        return this.hasPhysics;
    }

    public void setTicking(boolean ticking) {
        this.ticking = ticking;
    }

    public boolean isTicking() {
        return this.ticking;
    }
}
