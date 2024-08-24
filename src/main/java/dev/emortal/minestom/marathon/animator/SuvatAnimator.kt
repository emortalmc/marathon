package dev.emortal.minestom.marathon.animator

import dev.emortal.minestom.marathon.MarathonGame
import net.minestom.server.ServerFlag
import net.minestom.server.collision.Aerodynamics
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.other.FallingBlockMeta
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.timer.TaskSchedule
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

class SuvatAnimator : BlockAnimator {
    private var lastValidBlock: Point? = null

    override fun setBlockAnimated(instance: Instance, point: Point, block: Block, lastPoint: Point) {
        if (this.lastValidBlock == null) {
            this.lastValidBlock = lastPoint
        }
        val realLastPoint = lastValidBlock!!.add(0.5, 0.0, 0.5)

        //   comment from luis:
        // i have spent hours trying to replicate the old physics behaviour of this class.
        // i cannot, for the life of me, figure out why it isn't the same anymore. the same
        // gravity values are being put in, and the same velocities are being calculated.
        // i have now decided that this is good enough, and that someone else should fix it.
        val lastEntity = Entity(EntityType.FALLING_BLOCK)
        lastEntity.setTag<Boolean>(MarathonGame.Companion.MARATHON_ENTITY_TAG, true)
        lastEntity.aerodynamics = Aerodynamics(GRAVITY, 0.0, 0.0)
        lastEntity.editEntityMeta(FallingBlockMeta::class.java) { meta: FallingBlockMeta -> meta.block = block }

        val displacementY = point.y() - realLastPoint.y()
        val displacementXZ = point.sub(realLastPoint)
        val time = sqrt(-2 * HEIGHT / GRAVITY) + sqrt(2 * (displacementY - HEIGHT) / GRAVITY)
        val velocityY = sqrt(-GRAVITY * HEIGHT)
        val velocityXZ = displacementXZ.div(time)

        val combinedVelocity = Vec.fromPoint(velocityXZ.withY(velocityY))
        lastEntity.velocity = combinedVelocity.mul(ServerFlag.SERVER_TICKS_PER_SECOND.toDouble())

        lastEntity.setInstance(instance, realLastPoint)

        lastEntity.scheduler()
            .buildTask {
                lastEntity.remove()
                instance.setBlock(point, block)
                this.lastValidBlock = point
            }
            .delay(TaskSchedule.tick(max(ceil(time).toInt().toDouble(), 1.0).toInt()))
            .schedule()
    }

    override fun reset() {
        this.lastValidBlock = null
    }

    companion object {
        private const val HEIGHT = 2.2 // height to aim for
        private const val GRAVITY = -0.06
    }
}
