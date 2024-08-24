package dev.emortal.minestom.marathon.util

import net.minestom.server.adventure.audience.PacketGroupingAudience
import net.minestom.server.coordinate.Point
import net.minestom.server.instance.block.Block
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket
import net.minestom.server.network.packet.server.play.EffectPacket

interface BlockPacketUtils {
    companion object {
        fun sendBlockDamage(audience: PacketGroupingAudience, point: Point, destroyStage: Byte) {
            audience.sendGroupedPacket(BlockBreakAnimationPacket(1, point, destroyStage))
        }

        fun sendBlockBreak(audience: PacketGroupingAudience, point: Point, block: Block) {
            audience.sendGroupedPacket(EffectPacket(2001, point, block.stateId(), false))
        }
    }
}
