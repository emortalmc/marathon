package dev.emortal.minestom.marathon;

import dev.emortal.minestom.gamesdk.config.GameCreationInfo;
import dev.emortal.minestom.gamesdk.game.Game;
import dev.emortal.minestom.marathon.listener.MovementListener;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MarathonGameRunner extends Game {

    private final Map<UUID, MarathonGame> games = Collections.synchronizedMap(new HashMap<>());
    private final DynamicRegistry.Key<DimensionType> dimension;

    public MarathonGameRunner(@NotNull GameCreationInfo creationInfo, @NotNull DynamicRegistry.Key<DimensionType> dimension) {
        super(creationInfo);
        this.dimension = dimension;

        MovementListener movementListener = new MovementListener(this);
        this.getEventNode().addListener(PlayerMoveEvent.class, movementListener::onMove);
    }

    @Override
    public void start() {
    }

    @Override
    public void onJoin(@NotNull Player player) {
        MarathonGame game = this.games.computeIfAbsent(player.getUuid(), uuid -> new MarathonGame(player, this.dimension));
        game.onJoin(player);
    }

    @Override
    public void onLeave(@NotNull Player player) {
        // Do nothing - game SDK will check if player count is 0 and end the game
    }

    @Override
    public void cleanUp() {
        for (MarathonGame game : this.games.values()) {
            game.cleanUp();
        }
    }

    @Override
    public @NotNull Instance getSpawningInstance(@NotNull Player player) {
        MarathonGame game = this.games.get(player.getUuid());
        if (game == null) {
            throw new IllegalStateException("No game found for player " + player.getUsername() + " when instance requested!");
        }
        return game.getInstance();
    }

    public @Nullable MarathonGame getGameForPlayer(@NotNull Player player) {
        return this.games.get(player.getUuid());
    }
}
