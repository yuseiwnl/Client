package jp.client.module.impl.other

import jp.client.event.PacketEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.util.chat.ChatUtil
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C0APacketAnimation

object Logger : Module("Logger", Category.OTHER) {
    @SubscribeEvent
    fun onPacket(e: PacketEvent) {
        val packet = e.packet
        if (packet is C08PacketPlayerBlockPlacement) {
            ChatUtil.display("C08PacketPlayerBlockPlacement | " + mc.thePlayer.ticksExisted)
        }

        /*
        if (packet is C03PacketPlayer) {
            ChatUtil.display("C03PacketPlayer | "  + mc.thePlayer.ticksExisted)
        }
         */

        if (packet is C02PacketUseEntity && (packet.action == C02PacketUseEntity.Action.ATTACK || packet.action == C02PacketUseEntity.Action.INTERACT || packet.action == C02PacketUseEntity.Action.INTERACT_AT)) {
            ChatUtil.display("C02PacketUseEntity | " + packet.action.name + " | " + packet.hitVec + " | " + mc.thePlayer.ticksExisted)
        }

        if (packet is C0APacketAnimation) {
            ChatUtil.display("C0APacketAnimation | " + mc.thePlayer.ticksExisted)
        }

        /*
        if (packet is C07PacketPlayerDigging && packet.status == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
            ChatUtil.display("C07PacketPlayerDigging | " + mc.thePlayer.ticksExisted)
        }

         */
    }
}