package dev.emortal.minestom.marathon

import dev.emortal.minestom.gamesdk.MinestomGameServer
import dev.emortal.minestom.gamesdk.config.GameCreationInfo
import dev.emortal.minestom.gamesdk.config.GameSdkConfig
import net.minestom.server.MinecraftServer
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        MinestomGameServer.create {
            val dimensionType = DimensionType.builder().ambientLight(1f).build()
            val dimension =
                MinecraftServer.getDimensionTypeRegistry().register(NamespaceID.from("fullbright"), dimensionType)
            GameSdkConfig.builder()
                .minPlayers(1)
                .gameCreator { info: GameCreationInfo -> MarathonGameRunner(info, dimension) }
                .build()
        }
    }
}
