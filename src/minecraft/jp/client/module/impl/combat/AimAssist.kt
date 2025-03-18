package jp.client.module.impl.combat

import jp.client.Client
import jp.client.command.impl.Friend
import jp.client.event.MouseRotationEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.util.player.PlayerUtil
import jp.client.util.rotation.RotationUtil
import net.minecraft.item.ItemSword

object AimAssist : Module("AimAssist", Category.COMBAT) {

    @SubscribeEvent
    fun onMouseRotation(e: MouseRotationEvent) {
        if (!mc.gameSettings.keyBindAttack.isKeyDown
            || mc.thePlayer.heldItem?.item !is ItemSword
            || e.deltaX == 0f)
            return

        val target = mc.theWorld.playerEntities.filter { player ->
            player != mc.thePlayer
                    //&& 0.5 < mc.thePlayer.getDistanceToEntity(player)
                    && mc.thePlayer.getDistanceToEntity(player) < 3.6
                    && player.isEntityAlive
                    && mc.thePlayer.canEntityBeSeen(player)
                    && !PlayerUtil.sameTeam(player)
                    && RotationUtil.isInFOV(player, 70f)
                    && !Client.commandManager[Friend.javaClass].friends.contains(player)
        }.sortedBy { player ->
            mc.thePlayer.getDistanceToEntity(player)
        }.getOrNull(0)

        target?.let {
            val rotations = RotationUtil.updateRotation(mc.thePlayer.rotationYaw, RotationUtil.calculate(target).x, 7f)
            val yaw = (rotations - mc.thePlayer.rotationYaw)
            e.deltaX = (mc.mouseHelper.deltaX + yaw)
        }
    }
}