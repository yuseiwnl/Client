package jp.client.module.impl.other

import jp.client.Client
import jp.client.command.impl.Friend
import jp.client.event.GetCollisionBorderSizeEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.util.player.PlayerUtil
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemTool

object GhostHand : Module("GhostHand", Category.OTHER) {
    @SubscribeEvent
    fun onGetCollisionBorderSizeEvent(e: GetCollisionBorderSizeEvent) {
        if (e.entity is EntityLivingBase && PlayerUtil.sameTeam(e.entity)
            || e.entity is EntityLivingBase && Client.commandManager[Friend.javaClass].friends.contains(e.entity)
            || mc.thePlayer.heldItem?.item is ItemBlock
            || mc.thePlayer.heldItem?.item is ItemTool) {
            e.size = -0.3F
        }
    }
}