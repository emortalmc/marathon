package dev.emortal.minestom.marathon.listener;

import dev.emortal.minestom.marathon.MarathonGame;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public final class MovementListener {

    private final @NotNull MarathonGame game;

    public MovementListener(@NotNull MarathonGame game) {
        this.game = game;
    }

    public void onMove(@NotNull PlayerMoveEvent event) {
        Point posUnder = event.getNewPosition().sub(0, 1, 0);
        Point posTwoUnder = event.getNewPosition().sub(0, 2, 0);

        this.checkPosition(event.getNewPosition());

        int index = 0;
        for (Point block : this.game.getBlocks()) {
            if (block.sameBlock(posUnder)) break;
            if (block.sameBlock(posTwoUnder)) break;
            index++;
        }

        if (index <= 0 || index == this.game.getBlocks().size()) return;

        this.game.generateNextBlocks(index, true);
    }

    private void checkPosition(@NotNull Point newPosition) {
        int maxX = 0;
        int minX = 0;

        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;

        int maxZ = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (Point block : this.game.getBlocks()) {
            if (block.blockX() < minX) minX = block.blockX();
            if (block.blockX() > maxX) maxX = block.blockX();

            if (block.blockY() < minY) minY = block.blockY();
            if (block.blockY() > maxY) maxY = block.blockY();

            if (block.blockZ() < minZ) minZ = block.blockZ();
            if (block.blockZ() > maxZ) maxZ = block.blockZ();
        }

        if (newPosition.blockY() < (minY - 3)) { // too far below
            this.game.reset();
        }

        if (this.game.isRunInvalidated()) return;

        if (
                newPosition.blockY() > (maxY + 3.5) || // too far up
                        newPosition.blockZ() < minZ - 5 || // too far behind
                        newPosition.blockX() > maxX + 6 || // too far left and right
                        newPosition.blockX() < minX - 6
        ) {
            this.game.setRunInvalidated(true);
        }
    }

}
