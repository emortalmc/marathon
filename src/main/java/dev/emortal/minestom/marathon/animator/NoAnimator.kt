package dev.emortal.minestom.marathon.animator

import net.minestom.server.coordinate.Point
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block

class NoAnimator : BlockAnimator {
    override fun setBlockAnimated(instance: Instance, point: Point, block: Block, lastPoint: Point) {
        instance.setBlock(point, block)
    }

    override fun reset() {
        // do nothing
    }
}
