package dev.emortal.minestom.marathon.util;

import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket;
import net.minestom.server.network.packet.server.play.EffectPacket;
import org.jetbrains.annotations.NotNull;

public final class BlockPacketUtils {

    public static void sendBlockDamage(@NotNull PacketGroupingAudience audience, @NotNull Point point, byte destroyStage) {
        audience.sendGroupedPacket(new BlockBreakAnimationPacket(1, point, destroyStage));
    }

    public static void sendBlockBreak(@NotNull PacketGroupingAudience audience, @NotNull Point point, @NotNull Block block) {
        audience.sendGroupedPacket(new EffectPacket(2001, point, block.stateId(), false));
    }

}
