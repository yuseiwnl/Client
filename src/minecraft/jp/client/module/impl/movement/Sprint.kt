package jp.client.module.impl.movement

import jp.client.event.TickEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard

object Sprint : Module("Sprint", Category.MOVEMENT, Keyboard.KEY_I) {
    @SubscribeEvent
    fun onTick(e: TickEvent) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.keyCode, true)
    }

    override fun onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.keyCode, false)
        super.onDisable()
    }
}