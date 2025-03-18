package jp.client.module.impl.player

import jp.client.event.BlockBreakEvent
import jp.client.event.BlockDamageEvent
import jp.client.event.EventPriority
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.util.rotation.RaytraceUtil
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MouseHelper


object Nuker : Module("Nuker", Category.PLAYER) {
    var lastPos: BlockPos = BlockPos.ORIGIN
    lateinit var mouseHelper: MouseHelper
    var blacklist = ArrayList<BlockPos>()

    override fun onEnable() {
        super.onEnable()
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true)
        mc.thePlayer.rotationYaw = getYaw(mc.thePlayer.horizontalFacing)
        mc.thePlayer.rotationPitch = 45f
        mc.gameSettings.pauseOnLostFocus = false
        mouseHelper = mc.mouseHelper
        mc.mouseHelper.ungrabMouseCursor()
        mc.mouseHelper = object : MouseHelper() {
            override fun mouseXYChange() {
            }

            override fun grabMouseCursor() {
            }

            override fun ungrabMouseCursor() {
            }
        }

        lastPos = BlockPos.ORIGIN
    }

    override fun onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false)
        mc.mouseHelper = mouseHelper
        mc.mouseHelper.grabMouseCursor()
        mc.gameSettings.pauseOnLostFocus = true
        super.onDisable()
    }

    @SubscribeEvent(EventPriority.VERY_HIGH)
    fun onBlockDamage(e: BlockDamageEvent) {
        val blockPos = e.blockPos
        val block = mc.theWorld.getBlockState(blockPos).block

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, !(block == Blocks.chest || block == Blocks.trapped_chest))

        if (mc.thePlayer.isCollidedHorizontally && ((block != Blocks.chest && block != Blocks.trapped_chest && lastPos == blockPos) || block == Blocks.bedrock)) {
            blacklist.add(e.blockPos)
            var direction = EnumFacing.SOUTH
            for (enumFacing in EnumFacing.HORIZONTALS) {
                if (enumFacing == mc.thePlayer.horizontalFacing)
                    continue

                val yaw = getYaw(enumFacing)
                val mop = RaytraceUtil.getRotationOver2(yaw, 0f)

                if (mop == null)
                    continue

                if (blacklist.contains(mop.blockPos))
                    continue

                if (mc.theWorld.getBlockState(mop.blockPos).block == Blocks.bedrock || mc.theWorld.getBlockState(mop.blockPos).block == Blocks.air)
                    continue

                direction = enumFacing
            }

            mc.thePlayer.rotationYaw = getYaw(direction)
        }

        mc.thePlayer.inventory.currentItem = findTool(blockPos)
    }

    @SubscribeEvent
    fun onBlockBreak(e: BlockBreakEvent) {
        lastPos = e.blockPos
    }

    fun getYaw(enumFacing: EnumFacing): Float {
        return when (enumFacing) {
            EnumFacing.NORTH -> 180f
            EnumFacing.EAST -> -90f
            EnumFacing.SOUTH -> 0f
            EnumFacing.WEST -> 90f
            EnumFacing.DOWN -> TODO()
            EnumFacing.UP -> TODO()
        }
    }

    private fun findTool(blockPos: BlockPos): Int {
        var bestSpeed = 1f
        var bestSlot = 0

        val blockState = mc.theWorld.getBlockState(blockPos)

        for (i in 0..8) {
            val itemStack = mc.thePlayer.inventory.getStackInSlot(i) ?: continue

            val speed = itemStack.getStrVsBlock(blockState.block)

            if (speed > bestSpeed) {
                bestSpeed = speed
                bestSlot = i
            }
        }

        return bestSlot
    }
}