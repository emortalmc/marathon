package dev.emortal.minestom.marathon.util;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;

public class NoPhysicsEntity extends Entity {

    public NoPhysicsEntity(EntityType entityType) {
        super(entityType);
        hasPhysics = false;
    }
}
