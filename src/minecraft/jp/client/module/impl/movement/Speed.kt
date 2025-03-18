package jp.client.module.impl.movement

import jp.client.module.Module
import jp.client.module.setting.impl.ModeSetting

object Speed : Module("Speed", Category.MOVEMENT) {
    val mode = ModeSetting("Mode", this, "Legit", "Legit")

    /*
    @SubscribeEvent
    fun onStrafe(e: StrafeEvent) {
        if (mc.gameSettings.keyBindJump.isKeyDown && mc.thePlayer.onGround && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            val multiply: Double = 0.1 + Math.random() / 100.0
            mc.thePlayer.motionX *= 1.0 + (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1) * multiply
            mc.thePlayer.motionZ *= 1.0 + (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1) * multiply
        }
    }
     */
}