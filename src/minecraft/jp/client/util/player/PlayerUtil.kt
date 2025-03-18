package jp.client.util.player

import jp.client.util.InstanceAccess
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase

object PlayerUtil : InstanceAccess {
    fun sameTeam(entityLivingBase: EntityLivingBase): Boolean {
        val displayName = entityLivingBase.displayName.unformattedText
        val c1 = if (displayName.length > 1)
            displayName[1] else return false
        val c2 = if (mc.theWorld.scoreboard != null && mc.theWorld.scoreboard.getObjectiveInDisplaySlot(1) != null &&  mc.theWorld.scoreboard.getObjectiveInDisplaySlot(1).displayName.length > 1)
            mc.theWorld.scoreboard.getObjectiveInDisplaySlot(1).displayName[1] else mc.thePlayer.displayName.unformattedText[1]
        return c1 == c2
    }

    fun sendClick(button: Int, state: Boolean) {
        val keyBind = if (button == 0) mc.gameSettings.keyBindAttack.keyCode else mc.gameSettings.keyBindUseItem.keyCode
        KeyBinding.setKeyBindState(keyBind, state)
        if (state) {
            KeyBinding.onTick(keyBind)
        }
    }
}