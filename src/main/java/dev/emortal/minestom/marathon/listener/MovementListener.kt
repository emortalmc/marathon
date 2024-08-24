package dev.emortal.minestom.marathon.listener

import dev.emortal.minestom.marathon.MarathonGame
import dev.emortal.minestom.marathon.MarathonGameRunner
import net.minestom.server.coordinate.Point
import net.minestom.server.event.player.PlayerMoveEvent

class MovementListener(private val runner: MarathonGameRunner) {
    fun onMove(event: PlayerMoveEvent) {
        val game = runner.getGameForPlayer(event.player)
            ?: // Player is probably not in a game yet
            return

        val posUnder: Point = event.newPosition.sub(0.0, 1.0, 0.0)
        val posTwoUnder: Point = event.newPosition.sub(0.0, 2.0, 0.0)

        this.checkPosition(game, event.newPosition)

        var index = 0
        for (block in game.blocks) {
            if (block!!.sameBlock(posUnder)) break
            if (block.sameBlock(posTwoUnder)) break
            index++
        }

        if (index <= 0 || index == game.blocks.size) return

        game.generateNextBlocks(index, true)
    }

    private fun checkPosition(game: MarathonGame, newPosition: Point) {
        var maxX = 0
        var minX = 0

        var maxY = Int.MIN_VALUE
        var minY = Int.MAX_VALUE

        var maxZ = Int.MIN_VALUE
        var minZ = Int.MAX_VALUE

        for (block in game.blocks) {
            if (block!!.blockX() < minX) minX = block.blockX()
            if (block.blockX() > maxX) maxX = block.blockX()

            if (block.blockY() < minY) minY = block.blockY()
            if (block.blockY() > maxY) maxY = block.blockY()

            if (block.blockZ() < minZ) minZ = block.blockZ()
            if (block.blockZ() > maxZ) maxZ = block.blockZ()
        }

        if (newPosition.blockY() < (minY - 3)) { // too far below
            game.reset()
            game.teleportPlayerToStart()
        }

        if (game.isRunInvalidated) return

        if (// too far up
        // too far behind
        // too far left and right
            newPosition.blockY() > (maxY + 3.5) || newPosition.blockZ() < minZ - 5 || newPosition.blockX() > maxX + 6 || newPosition.blockX() < minX - 6
        ) {
            game.isRunInvalidated = true
        }
    }
}
