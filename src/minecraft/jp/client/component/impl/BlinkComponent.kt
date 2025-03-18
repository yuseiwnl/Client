package jp.client.component.impl

import jp.client.Client
import jp.client.component.Component
import jp.client.event.EventPriority
import jp.client.event.LoadWorldEvent
import jp.client.event.PacketEvent
import jp.client.event.annotations.SubscribeEvent
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C09PacketHeldItemChange
import java.util.concurrent.ConcurrentLinkedQueue

object BlinkComponent : Component() {
    var blinking = false
    private val packets: ConcurrentLinkedQueue<Packet<*>> = ConcurrentLinkedQueue<Packet<*>>()

    @SubscribeEvent(priority = EventPriority.VERY_HIGH)
    fun onSendPacket(e: PacketEvent) {
        if (e.state == PacketEvent.State.RECEIVE || !blinking) {
            return
        }

        val packet = e.packet
        packets.add(packet)
        e.canceled = true
    }

    @SubscribeEvent
    fun onLoadWorld(e: LoadWorldEvent) {
        packets.clear()
        blinking = false
    }

    fun dispatch() {
        try {
            synchronized(packets) {
                for (packet in packets) {
                    if (packet is C09PacketHeldItemChange) {
                        Client.packetManager.playerSlot = packet.slotId
                    }
                    mc.netHandler.networkManager.sendPacketNoEvent(packet)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //ChatUtil.display("&cThere was an error releasing blinked packets")
        }
        packets.clear()
        blinking = false
    }
}