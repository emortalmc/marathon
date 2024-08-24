package dev.emortal.minestom.marathon.generator

import net.minestom.server.coordinate.Point

interface Generator {
    fun getNextPoint(current: Point, targetY: Int, score: Int): Point
}
