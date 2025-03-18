package jp.client.module.impl.combat

import jp.client.event.MouseOverEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module

object Reach : Module("Reach", Category.COMBAT) {
    @SubscribeEvent
    fun onMouseOver(e: MouseOverEvent) {
        e.range = 3.1
    }
}