package jp.client.component.impl

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.vialoadingbase.ViaLoadingBase
import jp.client.component.Component
import jp.client.event.EventPriority
import jp.client.event.MinimumMotionEvent
import jp.client.event.PacketEvent
import jp.client.event.annotations.SubscribeEvent
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement


object ViaMCPComponent : Component() {
    private var lastGround = false

    @SubscribeEvent
    fun onPacket(e: PacketEvent) {
        val packet: Packet<*> = e.packet
        if (ViaLoadingBase.getInstance().targetVersion.newerThanOrEqualTo(ProtocolVersion.v1_11)) {
            if (packet is C08PacketPlayerBlockPlacement) {
                packet.facingX /= 16.0f
                packet.facingY /= 16.0f
                packet.facingZ /= 16.0f
            }
        }

        if (ViaLoadingBase.getInstance().targetVersion.newerThan(ProtocolVersion.v1_8)) {
            if (packet is C03PacketPlayer) {
                if (!packet.isMoving && !packet.rotating && packet.onGround == this.lastGround) {
                    e.canceled = true
                }

                this.lastGround = packet.onGround
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.VERY_LOW)
    fun onPacket2(e: PacketEvent) {
        if (!e.canceled && ViaLoadingBase.getInstance()
                .targetVersion.newerThan(ProtocolVersion.v1_8)
        ) {
            if (e.packet is C02PacketUseEntity) {
                e.canceled = e.canceled || (e.packet as C02PacketUseEntity).action != C02PacketUseEntity.Action.ATTACK
            }
        }
    }

    @SubscribeEvent
    fun onMinimumMotion(e: MinimumMotionEvent) {
        if (ViaLoadingBase.getInstance().targetVersion.newerThan(ProtocolVersion.v1_8)) {
            e.motion = 0.003
        }
    }
}