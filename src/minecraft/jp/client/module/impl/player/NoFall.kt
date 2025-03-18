package jp.client.module.impl.player

import jp.client.event.MotionEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import net.minecraft.network.play.client.C03PacketPlayer

object NoFall : Module("NoFall", Category.PLAYER) {
    override fun onDisable() {
        mc.timer.timerSpeed = 1.0F
        super.onDisable()
    }

    @SubscribeEvent
    fun onMotion(e: MotionEvent) {
        if (e.state == MotionEvent.State.POST)
            return

        mc.timer.timerSpeed = 1.0F
        if (mc.thePlayer.motionY < 0 && mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3.0) {
            mc.timer.timerSpeed = 0.5F
            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
            mc.thePlayer.fallDistance = 0F
        }
    }
}