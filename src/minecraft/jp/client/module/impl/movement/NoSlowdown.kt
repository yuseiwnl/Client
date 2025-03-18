package jp.client.module.impl.movement

import jp.client.event.MotionEvent
import jp.client.event.PacketEvent
import jp.client.event.SlowdownEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.module.setting.impl.ModeSetting
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos

object NoSlowdown : Module("NoSlowdown", Category.MOVEMENT) {
    val mode = ModeSetting("Mode", this, "Watchdog", "Watchdog")

    var offGroundTicks = 0
    var send = false

    @SubscribeEvent
    fun onSlowdown(e: SlowdownEvent) {
        if ((mc.thePlayer.heldItem?.item is ItemBow || mc.thePlayer.heldItem?.item is ItemFood || mc.thePlayer.heldItem?.item is ItemPotion) && e.type == SlowdownEvent.Type.NO_SLOW)
            e.canceled = true
    }

    @SubscribeEvent
    fun onPacket(e: PacketEvent) {
        if (e.state == PacketEvent.State.RECEIVE)
            return

        val packet = e.packet

        if (packet is C08PacketPlayerBlockPlacement && !mc.thePlayer.isUsingItem) {
            if ((mc.thePlayer.heldItem?.item is ItemBow || mc.thePlayer.heldItem?.item is ItemFood || mc.thePlayer.heldItem?.item is ItemPotion) && packet.placedBlockDirection == 255 && offGroundTicks < 2) {
                if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown) {
                    mc.thePlayer.jump()
                }
                send = true
                e.canceled = true
            }
        }
    }

    @SubscribeEvent
    fun onMotion(e: MotionEvent) {
        if (e.state == MotionEvent.State.POST)
            return

        if (mc.thePlayer.onGround) {
            offGroundTicks = 0
        } else {
            offGroundTicks++
        }

        val item = mc.thePlayer.heldItem
        if (offGroundTicks == 4 && send) {
            send = false;
            mc.netHandler.networkManager.sendPacketNoEvent(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, item, 0F, 0F, 0F))

        } else if (item != null && mc.thePlayer.isUsingItem) {
            e.posY = e.posY + 1E-14
        }
    }
}