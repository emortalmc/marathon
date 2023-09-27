package dev.emortal.minestom.marathon;

import dev.emortal.minestom.gamesdk.MinestomGameServer;
import dev.emortal.minestom.gamesdk.config.GameSdkConfig;
import net.minestom.server.MinecraftServer;

public final class Main {

    public static void main(String[] args) {
        MinestomGameServer.create(() -> {
            MinecraftServer.getDimensionTypeManager().addDimension(MarathonGame.FULLBRIGHT_DIMENSION);

            return GameSdkConfig.builder()
                    .minPlayers(1)
                    .maxGames(50)
                    .gameCreator(MarathonGame::new)
                    .build();
        });
    }
}
