package dev.emortal.minestom.marathon.generator

import net.minestom.server.coordinate.Point
import java.util.concurrent.ThreadLocalRandom

class DefaultGenerator : Generator {
    override fun getNextPoint(current: Point, targetY: Int, score: Int): Point {
        val random = ThreadLocalRandom.current()

        var y = -1
        if (targetY == 0) y = random.nextInt(-1, 2)
        if (targetY > current.blockY()) y = 1

        val z = when (y) {
            1 -> random.nextInt(1, 3)
            -1 -> random.nextInt(2, 5)
            else -> random.nextInt(1, 4)
        }

        val x = random.nextInt(-3, 4)

        return current.add(x.toDouble(), y.toDouble(), z.toDouble())
    }

    companion object {
        val INSTANCE: Generator = DefaultGenerator()
    }
}
