package jp.client.util.packet

import jp.client.Client
import jp.client.event.EventPriority
import jp.client.event.MotionEvent
import jp.client.event.PacketEvent
import jp.client.event.annotations.SubscribeEvent
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.server.S09PacketHeldItemChange
import net.minecraft.network.play.server.S0CPacketSpawnPlayer

class PacketManager {
    var C08: Boolean = false
    var C07: Boolean = false
    private var C02 = false
    var C09: Boolean = false
    var delayAttack: Boolean = false
    var delay: Boolean = false
    var playerSlot: Int = -1
    var serverSlot: Int = -1

    init {
        Client.eventBus.register(this)
    }

    @SubscribeEvent(priority = EventPriority.VERY_HIGH)
    fun onSendPacket(e: PacketEvent) {
        if (e.state == PacketEvent.State.RECEIVE || e.canceled) {
            return
        }
        if (e.packet is C02PacketUseEntity) { // sending a C07 on the same tick as C02 can ban, this usually happens when you unblock and attack on the same tick
            if (C07) {
                e.canceled = true
                return
            }
            C02 = true
        } else if (e.packet is C08PacketPlayerBlockPlacement) {
            C08 = true
        } else if (e.packet is C07PacketPlayerDigging) {
            C07 = true
        } else if (e.packet is C09PacketHeldItemChange) {
            if ((e.packet as C09PacketHeldItemChange).slotId == playerSlot && (e.packet as C09PacketHeldItemChange).slotId == serverSlot) {
                e.canceled = true
                return
            }
            C09 = true
            playerSlot = (e.packet as C09PacketHeldItemChange).slotId
            serverSlot = playerSlot
        }
    }

    @SubscribeEvent
    fun onReceivePacket(e: PacketEvent) {
        if (e.state == PacketEvent.State.SEND)
            return

        if (e.packet is S09PacketHeldItemChange) {
            val packet = e.packet as S09PacketHeldItemChange
            if (packet.heldItemHotbarIndex >= 0 && packet.heldItemHotbarIndex < InventoryPlayer.getHotbarSize()) {
                serverSlot = packet.heldItemHotbarIndex
            }
        } else if (e.packet is S0CPacketSpawnPlayer && Minecraft.getMinecraft().thePlayer != null) {
            if ((e.packet as S0CPacketSpawnPlayer).entityID != Minecraft.getMinecraft().thePlayer.entityId) {
                return
            }
            this.playerSlot = -1
        }
    }

    @SubscribeEvent(priority = EventPriority.VERY_HIGH)
    fun onPostUpdate(e: MotionEvent) {
        if (e.state == MotionEvent.State.PRE)
            return

        if (delay) {
            delayAttack = false
            delay = false
        }
        if (C08 || C09) {
            delay = true
            delayAttack = true
        }
        C09 = false
        C02 = C09
        C07 = C02
        C08 = C07
    }
}