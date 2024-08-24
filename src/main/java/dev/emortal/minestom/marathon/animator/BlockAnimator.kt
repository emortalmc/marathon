package dev.emortal.minestom.marathon.animator

import net.minestom.server.coordinate.Point
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block

interface BlockAnimator {
    fun setBlockAnimated(instance: Instance, point: Point, block: Block, lastPoint: Point)

    fun destroyBlockAnimated(instance: Instance, point: Point, block: Block) {
        instance.setBlock(point, Block.AIR)
    }

    fun reset()
}
