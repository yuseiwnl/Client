package jp.client.module.impl.combat

import jp.client.event.MoveInputEvent
import jp.client.event.PacketEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.module.setting.impl.NumberSetting
import net.minecraft.item.ItemSword
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.MathHelper.wrapAngleTo180_float
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

object Velocity : Module("Velocity", Category.COMBAT) {
    private val horizontal = NumberSetting("Horizontal", this, 100, 0, 100)

    private var jump = false

    @SubscribeEvent
    fun onPacket(e: PacketEvent) {
        if (e.state == PacketEvent.State.SEND || mc.thePlayer == null || mc.thePlayer.heldItem?.item !is ItemSword)
            return

        val packet = e.packet

        if (packet is S12PacketEntityVelocity && packet.entityID == mc.thePlayer.entityId && isInFOV(packet.motionX.toFloat(), packet.motionZ.toFloat(), 90f)) {
            //e.canceled = true
            //mc.thePlayer.motionY = packet.motionY / 8000.0
            val percentage = horizontal.setting.toInt() / 100.0
            packet.motionX = (packet.motionX * percentage).toInt()
            packet.motionZ = (packet.motionZ * percentage).toInt()

            //if (mc.thePlayer.onGround)
                //jump = true
        }
    }

    @SubscribeEvent
    fun onMove(e: MoveInputEvent) {
        if (jump) {
            if (!mc.gameSettings.keyBindJump.isKeyDown) {
                e.jump = true
            }
            jump = false
        }
    }

    private fun calculate(motionX: Float, motionZ: Float): Float {
        val yaw = (atan2(-motionZ, -motionX) * 180.0F / PI).toFloat() - 90.0f
        return mc.thePlayer.rotationYaw + wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw)
    }

    private fun isInFOV(motionX: Float, motionZ: Float, fov: Float): Boolean {
        val differenceYaw = abs(mc.thePlayer.rotationYaw - calculate(motionX, motionZ))
        return differenceYaw <= fov
    }
}