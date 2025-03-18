package jp.client.module.impl.visual

import jp.client.event.PacketEvent
import jp.client.event.TickEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import net.minecraft.network.play.server.S03PacketTimeUpdate

object Ambience : Module("Ambience", Category.VISUAL) {
    @SubscribeEvent
    fun onTick(e: TickEvent) {
        if (mc.thePlayer == null) return

        mc.theWorld.worldTime = 18000
    }

    @SubscribeEvent
    fun onPacketReceive(e: PacketEvent) {
        if (mc.thePlayer == null || e.state == PacketEvent.State.SEND) return

        if (e.packet is S03PacketTimeUpdate)
            e.canceled = true
    }
}