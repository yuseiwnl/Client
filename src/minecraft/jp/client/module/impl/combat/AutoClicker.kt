package jp.client.module.impl.combat

import jp.client.event.TickEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.util.Stopwatch
import jp.client.util.player.PlayerUtil
import net.minecraft.client.settings.KeyBinding
import net.minecraft.item.ItemSword
import net.minecraft.util.MovingObjectPosition
import org.lwjgl.input.Mouse
import java.util.concurrent.ThreadLocalRandom

object AutoClicker : Module("AutoClicker", Category.COMBAT) {
    private var blockHit = false
    private val stopwatch: Stopwatch = Stopwatch()
    private var nextSwing: Long = 0
    private var ticksDown = 0
    private var ticksDownRight: Int = 0

    @SubscribeEvent
    fun onTick(e: TickEvent) {
        if (mc.currentScreen != null)
            return

        if (mc.thePlayer.heldItem?.item !is ItemSword || mc.gameSettings.keyBindAttack.isKeyDown && mc.objectMouseOver?.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK || !mc.gameSettings.keyBindAttack.isKeyDown) {
            reset()
            return
        }

        if (stopwatch.finished(nextSwing)) {
            nextSwing = (1000 / (ThreadLocalRandom.current().nextInt(15, 25))).toLong()

            if (!mc.thePlayer.isUsingItem && ticksDown > 0) {
                //if (mc.objectMouseOver?.typeOfHit == MovingObjectPosition.MovingObjectType.MISS || (mc.objectMouseOver?.entityHit as EntityLivingBase).hurtTime <= 4)
                PlayerUtil.sendClick(0, true)
            }

            if (mc.thePlayer.heldItem?.item is ItemSword && Mouse.isButtonDown(1)) {
                blockHit = true

                if (mc.gameSettings.keyBindUseItem.isKeyDown) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, false)
                }

                if (!mc.thePlayer.isUsingItem && ticksDownRight > 0)
                    KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
            } else reset()

            stopwatch.reset()
        }

        ticksDown = if (Mouse.isButtonDown(0)) ticksDown + 1 else 0
        ticksDownRight = if (Mouse.isButtonDown(1)) ticksDownRight + 1 else 0
    }

    private fun reset() {
        if (blockHit) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, Mouse.isButtonDown(1))
            blockHit = false
        }
    }
}