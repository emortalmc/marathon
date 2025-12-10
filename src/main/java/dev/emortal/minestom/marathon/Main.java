package dev.emortal.minestom.marathon;

import dev.emortal.api.model.gamedata.GameDataGameMode;
import dev.emortal.api.model.gamedata.V1MarathonData;
import dev.emortal.api.utils.GrpcStubCollection;
import dev.emortal.minestom.core.module.messaging.MessagingModule;
import dev.emortal.minestom.gamesdk.MinestomGameServer;
import dev.emortal.minestom.gamesdk.config.GameSdkConfig;
import dev.emortal.minestom.gamesdk.util.GamePlayerDataRepository;
import dev.emortal.minestom.marathon.options.BlockPalette;
import dev.emortal.minestom.marathon.options.Time;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.ShadowColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.Color;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.attribute.EnvironmentAttribute;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Main {
    public static final @NotNull V1MarathonData DEFAULT_PLAYER_DATA = V1MarathonData.newBuilder()
            .setTime(Time.MIDNIGHT.name())
            .setBlockPalette(BlockPalette.OVERWORLD.name())
            .build();

    static void main() {
        MinestomGameServer.create(moduleManager -> {

            DimensionType overworld = MinecraftServer.getDimensionTypeRegistry().get(DimensionType.OVERWORLD);

            DimensionType dimensionType = DimensionType.builder()
                    .timelines(overworld.timelines())
                    .setAttribute(EnvironmentAttribute.CLOUD_COLOR, ShadowColor.fromHexString("#ccffffff"))
                    .setAttribute(EnvironmentAttribute.FOG_COLOR, new Color(0xc0d8ff))
                    .setAttribute(EnvironmentAttribute.SKY_COLOR, new Color(0x78a7ff))
                    .setAttribute(EnvironmentAttribute.CLOUD_HEIGHT, 110f)
                    .ambientLight(1F)
                    .build();
            RegistryKey<DimensionType> dimension = MinecraftServer.getDimensionTypeRegistry().register(Key.key("fullbright"), dimensionType);

            GamePlayerDataRepository<V1MarathonData> playerStorage = new GamePlayerDataRepository<>(
                    GrpcStubCollection.getGamePlayerDataService().orElse(null), DEFAULT_PLAYER_DATA,
                    V1MarathonData.class, GameDataGameMode.MARATHON
            );

            return GameSdkConfig.builder()
                    .minPlayers(1)
                    .gameCreator(info -> {
                        Map<UUID, V1MarathonData> playerData;

                        try {
                            playerData = playerStorage.getPlayerData(info.playerIds());
                        } catch (Exception e) {
                            playerData = info.playerIds().stream().collect(Collectors.toMap(id -> id, id -> DEFAULT_PLAYER_DATA));
                        }

                        return new MarathonGameRunner(info, moduleManager.getModule(MessagingModule.class), dimension, playerData);
                    })
                    .build();
        });
    }
}
