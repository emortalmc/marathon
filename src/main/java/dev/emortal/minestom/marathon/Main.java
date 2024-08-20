package dev.emortal.minestom.marathon;

import dev.emortal.minestom.gamesdk.MinestomGameServer;
import dev.emortal.minestom.gamesdk.config.GameSdkConfig;
import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

public final class Main {

    public static void main(String[] args) {
        MinestomGameServer.create(() -> {
            DimensionType dimensionType = DimensionType.builder().ambientLight(1F).build();
            DynamicRegistry.Key<DimensionType> dimension = MinecraftServer.getDimensionTypeRegistry().register(NamespaceID.from("fullbright"), dimensionType);

            return GameSdkConfig.builder()
                    .minPlayers(1)
                    .gameCreator(info -> new MarathonGameRunner(info, dimension))
                    .build();
        });
    }
}
