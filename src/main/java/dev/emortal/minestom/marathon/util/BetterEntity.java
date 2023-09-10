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

public final class BetterEntity extends Entity {

    private boolean hasDrag = true;
    private boolean hasGravityDrag = true;

    public BetterEntity(@NotNull EntityType entityType) {
        super(entityType);
    }

    @Override
    protected void updateVelocity(boolean wasOnGround, boolean flying, @NotNull Pos positionBeforeMove, @NotNull Vec newVelocity) {
        EntitySpawnType type = this.entityType.registry().spawnType();
        double airDrag = type == EntitySpawnType.LIVING || type == EntitySpawnType.PLAYER ? 0.91 : 0.98;

        double drag;
        if (wasOnGround) {
            Chunk chunk = ChunkUtils.retrieve(this.instance, this.currentChunk, this.position);
            synchronized (chunk) {
                drag = chunk.getBlock(positionBeforeMove.sub(0, 0.5000001, 0)).registry().friction() * airDrag;
            }
        } else {
            drag = airDrag;
        }

        double gravity = flying ? 0 : this.gravityAcceleration;
        double gravityDrag;

        if (!this.hasGravityDrag) {
            gravityDrag = 1.0;
        } else {
            gravityDrag = flying ? 0.6 : (1 - this.gravityDragPerTick);
        }
        if (!this.hasDrag) drag = 1.0;

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
        super.tick(time);
    }

    public void setDrag(boolean drag) {
        this.hasDrag = drag;
    }

    public void setGravityDrag(boolean drag) {
        this.hasGravityDrag = drag;
    }

    public void setPhysics(boolean physics) {
        super.hasPhysics = physics;
    }
}
