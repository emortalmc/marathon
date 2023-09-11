package dev.emortal.minestom.marathon;

import dev.emortal.minestom.gamesdk.MinestomGameServer;
import dev.emortal.minestom.gamesdk.config.GameSdkConfig;
import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

public final class Main {

    public static void main(String[] args) {
        MinestomGameServer.create(() -> {
            MinecraftServer.getDimensionTypeManager().addDimension(MarathonGame.FULLBRIGHT_DIMENSION);

            return GameSdkConfig.builder()
                    .minPlayers(1)
                    .maxGames(30)
                    .gameCreator(MarathonGame::new)
                    .build();
        });
    }
}
