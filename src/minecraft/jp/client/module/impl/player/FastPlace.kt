package jp.client.module.impl.player

import jp.client.event.TickEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import net.minecraft.item.ItemBlock
import org.lwjgl.input.Mouse

object FastPlace : Module("FastPlace", Category.PLAYER) {
    private var ticksDownRight: Int = 0

    @SubscribeEvent
    fun onTick(e: TickEvent) {
        if (mc.currentScreen != null)
            return

        if (mc.thePlayer.heldItem?.item is ItemBlock && ticksDownRight > 1)
            mc.rightClickDelayTimer = Math.min(mc.rightClickDelayTimer, 1)

        if (Mouse.isButtonDown(1)) {
            ticksDownRight++
        } else {
            ticksDownRight = 0
        }
    }

    fun canFastPlace(): Boolean {
        return this.isToggled() && mc.thePlayer.heldItem?.item is ItemBlock && ticksDownRight > 1
    }
}