package dev.emortal.minestom.marathon.util;

import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.coordinate.Point;
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket;
import org.jetbrains.annotations.NotNull;

public interface BlockPacketUtils {
    static void sendBlockDamage(@NotNull PacketGroupingAudience audience, @NotNull Point point, byte destroyStage) {
        audience.sendGroupedPacket(new BlockBreakAnimationPacket(1, point, destroyStage));
    }
}
