package jp.client.module.impl.combat

import jp.client.event.SlowdownEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import org.lwjgl.input.Keyboard

object KeepSprint : Module("KeepSprint", Category.COMBAT, Keyboard.KEY_B) {
    @SubscribeEvent
    fun onSlowdown(e: SlowdownEvent) {
        if (e.type == SlowdownEvent.Type.KEEP_SPRINT)
            e.canceled = true
    }
}