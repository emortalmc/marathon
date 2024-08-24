package dev.emortal.minestom.marathon

import dev.emortal.minestom.gamesdk.config.GameCreationInfo
import dev.emortal.minestom.gamesdk.game.Game
import dev.emortal.minestom.marathon.listener.MovementListener
import net.minestom.server.entity.Player
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.instance.Instance
import net.minestom.server.registry.DynamicRegistry
import net.minestom.server.world.DimensionType
import java.util.*

class MarathonGameRunner(creationInfo: GameCreationInfo, private val dimension: DynamicRegistry.Key<DimensionType?>) :
    Game(creationInfo) {
    private val games: MutableMap<UUID, MarathonGame> = Collections.synchronizedMap(HashMap())

    init {
        val movementListener = MovementListener(this)

        eventNode
            .addListener(PlayerMoveEvent::class.java) { event: PlayerMoveEvent -> movementListener.onMove(event) }
            .addListener(PlayerSwapItemEvent::class.java) { event: PlayerSwapItemEvent -> event.isCancelled = true }
            .addListener(ItemDropEvent::class.java) { event: ItemDropEvent -> event.isCancelled = true }
    }

    override fun start() {
    }

    override fun onJoin(player: Player) {
        val game =
            games.computeIfAbsent(player.uuid) { uuid: UUID? -> MarathonGame(player, this.dimension) }
        game.onJoin(player)
    }

    override fun onLeave(player: Player) {
        // Do nothing - game SDK will check if player count is 0 and end the game
    }

    override fun cleanUp() {
        for (game in games.values) {
            game.cleanUp()
        }
    }

    override fun getSpawningInstance(player: Player): Instance {
        val game = games[player.uuid]
            ?: throw IllegalStateException("No game found for player " + player.username + " when instance requested!")
        return game.instance
    }

    fun getGameForPlayer(player: Player): MarathonGame? {
        return games[player.uuid]
    }
}
