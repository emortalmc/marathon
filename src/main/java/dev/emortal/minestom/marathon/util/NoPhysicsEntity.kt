package dev.emortal.minestom.marathon.util

import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType

class NoPhysicsEntity(entityType: EntityType?) : Entity(
    entityType!!
) {
    init {
        hasPhysics = false
    }
}
