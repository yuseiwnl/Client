package jp.client.module.impl.other

import jp.client.event.MouseRotationEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import org.lwjgl.input.Keyboard

object Derp : Module("Derp", Category.OTHER) {
    @SubscribeEvent
    fun onTick(e: MouseRotationEvent) {
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            e.deltaX += 10f
    }
}