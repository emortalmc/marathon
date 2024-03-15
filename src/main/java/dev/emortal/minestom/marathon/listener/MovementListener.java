package dev.emortal.minestom.marathon.listener;

import dev.emortal.minestom.marathon.MarathonGame;
import dev.emortal.minestom.marathon.MarathonGameRunner;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public final class MovementListener {

    private final @NotNull MarathonGameRunner runner;

    public MovementListener(@NotNull MarathonGameRunner runner) {
        this.runner = runner;
    }

    public void onMove(@NotNull PlayerMoveEvent event) {
        MarathonGame game = this.runner.getGameForPlayer(event.getPlayer());
        if (game == null) {
            // Player is probably not in a game yet
            return;
        }

        Point posUnder = event.getNewPosition().sub(0, 1, 0);
        Point posTwoUnder = event.getNewPosition().sub(0, 2, 0);

        this.checkPosition(game, event.getNewPosition());

        int index = 0;
        for (Point block : game.getBlocks()) {
            if (block.sameBlock(posUnder)) break;
            if (block.sameBlock(posTwoUnder)) break;
            index++;
        }

        if (index <= 0 || index == game.getBlocks().size()) return;

        game.generateNextBlocks(index, true);
    }

    private void checkPosition(@NotNull MarathonGame game, @NotNull Point newPosition) {
        int maxX = 0;
        int minX = 0;

        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;

        int maxZ = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (Point block : game.getBlocks()) {
            if (block.blockX() < minX) minX = block.blockX();
            if (block.blockX() > maxX) maxX = block.blockX();

            if (block.blockY() < minY) minY = block.blockY();
            if (block.blockY() > maxY) maxY = block.blockY();

            if (block.blockZ() < minZ) minZ = block.blockZ();
            if (block.blockZ() > maxZ) maxZ = block.blockZ();
        }

        if (newPosition.blockY() < (minY - 3)) { // too far below
            game.reset();
            game.teleportPlayerToStart();
        }

        if (game.isRunInvalidated()) return;

        if (
                newPosition.blockY() > (maxY + 3.5) || // too far up
                        newPosition.blockZ() < minZ - 5 || // too far behind
                        newPosition.blockX() > maxX + 6 || // too far left and right
                        newPosition.blockX() < minX - 6
        ) {
            game.setRunInvalidated(true);
        }
    }

}
