package dev.emortal.minestom.marathon.animator

import dev.emortal.minestom.marathon.MarathonGame
import dev.emortal.minestom.marathon.util.NoPhysicsEntity
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.BlockDisplayMeta
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.timer.TaskSchedule

class ScaleAnimator : BlockAnimator {
    override fun setBlockAnimated(instance: Instance, point: Point, block: Block, lastPoint: Point) {
        val effectDuration = 3

        val entity: Entity = NoPhysicsEntity(EntityType.BLOCK_DISPLAY)
        entity.setNoGravity(true)
        entity.setTag<Boolean>(MarathonGame.Companion.MARATHON_ENTITY_TAG, true)
        entity.editEntityMeta(BlockDisplayMeta::class.java) { meta: BlockDisplayMeta ->
            meta.setBlockState(Block.AIR)
            meta.scale = Vec(0.5)
            meta.translation = Vec(-0.25, 0.0, -0.25)
            meta.transformationInterpolationStartDelta = 0
            meta.transformationInterpolationDuration = effectDuration
        }

        entity.setInstance(instance, point).thenRun {
            entity.scheduler().buildTask {
                entity.editEntityMeta(
                    BlockDisplayMeta::class.java
                ) { meta: BlockDisplayMeta ->
                    meta.setBlockState(block)
                    meta.translation = Vec(-0.5, 0.0, -0.5)
                    meta.scale = Vec(1.0)
                }
            }.delay(TaskSchedule.tick(2)).schedule()
        }

        entity.scheduler()
            .buildTask {
                instance.setBlock(point, block)
                entity.scheduleNextTick { obj: Entity -> obj.remove() }
            }
            .delay(TaskSchedule.tick(effectDuration + 4))
            .schedule()
    }

    override fun reset() {
    }
}
