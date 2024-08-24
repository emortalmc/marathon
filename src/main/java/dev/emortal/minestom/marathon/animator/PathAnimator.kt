package dev.emortal.minestom.marathon.animator

import dev.emortal.minestom.marathon.MarathonGame
import dev.emortal.minestom.marathon.util.NoPhysicsEntity
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.BlockDisplayMeta
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.timer.TaskSchedule
import java.util.function.Consumer

class PathAnimator : BlockAnimator {
    private var lastEntity: Entity? = null

    override fun setBlockAnimated(instance: Instance, point: Point, block: Block, lastPoint: Point) {
        var realLastPoint = Pos.fromPoint(lastPoint.add(0.5, 0.0, 0.5))
        if (this.lastEntity != null && !lastEntity!!.isRemoved) {
            realLastPoint = lastEntity!!.position
        }

        this.lastEntity = NoPhysicsEntity(EntityType.BLOCK_DISPLAY)
        lastEntity.setTag<Boolean>(MarathonGame.Companion.MARATHON_ENTITY_TAG, true)
        lastEntity.setNoGravity(true)
        lastEntity.editEntityMeta(BlockDisplayMeta::class.java, Consumer { meta: BlockDisplayMeta ->
            meta.setBlockState(block)
            meta.translation = Vec(-0.5, 0.0, -0.5)
            meta.posRotInterpolationDuration = 3
        })
        val finalEntity = this.lastEntity

        lastEntity.setInstance(instance, realLastPoint).thenRun {
            finalEntity!!.scheduler().buildTask { finalEntity.teleport(Pos.fromPoint(point)) }
                .repeat(TaskSchedule.tick(1))
                .schedule()
        }

        lastEntity.scheduler()
            .buildTask {
                instance.setBlock(point, block)
                finalEntity!!.scheduleNextTick { obj: Entity -> obj.remove() }
            }
            .delay(TaskSchedule.tick(13))
            .schedule()
    }

    override fun reset() {
        this.lastEntity = null
    }
}
