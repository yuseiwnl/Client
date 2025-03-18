package jp.client.module.impl.combat

import jp.client.Client
import jp.client.command.impl.Friend
import jp.client.component.impl.RotationComponent
import jp.client.event.EventPriority
import jp.client.event.MouseOverEvent
import jp.client.event.Render3DEvent
import jp.client.event.TickEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.module.setting.impl.BooleanSetting
import jp.client.module.setting.impl.ModeSetting
import jp.client.module.setting.impl.NumberSetting
import jp.client.util.Stopwatch
import jp.client.util.chat.ChatUtil
import jp.client.util.player.PlayerUtil
import jp.client.util.render.ColorUtil
import jp.client.util.render.RenderUtil
import jp.client.util.rotation.RaytraceUtil
import jp.client.util.rotation.RotationUtil
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSword
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom


object KillAura : Module("KillAura", Category.COMBAT, Keyboard.KEY_K) {
    private val fov = NumberSetting("FOV", this, 45f, 0f, 180f)
    private val mode = ModeSetting("Mode", this, "Switch", "Single", "Switch")
    private val range = NumberSetting("Range", this, 3.0, 3.0, 6.0)
    private val requireMouseDown = BooleanSetting("Require Mouse Down", this, true)
    private val teams = BooleanSetting("Teams", this, true)

    private var index = 0
    private var nextSwing: Long = 0
    private val stopwatch = Stopwatch()
    private val switchTimer = Stopwatch()
    var target: EntityLivingBase? = null
    private var blocking = false
    private var blockHit = false
    private var ticksDownRight: Int = 0

    @SubscribeEvent(priority = EventPriority.HIGH)
    fun onHighPreUpdate(e: TickEvent) {
        if (!canAttack()) {
            target = null
            return
        }

        val targets = getTargets()
        if (switchTimer.finished(100)) {
            index++
            switchTimer.reset()
        }

        if (targets.size <= index)
            index = 0

        target = targets.getOrNull(index)

        /*
        target = getTargets().sortedBy {
            //RotationUtil.getYawDifference(it)
            RotationUtil.calculatePerfectRangeToEntity(it)
            //mc.thePlayer.getDistanceToEntity(it)
        }.getOrNull(0)

         */

        target?.let {
            val rotation = RotationUtil.calculate(it, true, range.setting.toDouble())
            RotationComponent.setRotations(rotation.x, rotation.y)
        }
    }

    @SubscribeEvent
    fun onPreUpdate(e: TickEvent) {
        if (mc.thePlayer.heldItem?.item !is ItemSword || !mc.gameSettings.keyBindAttack.isKeyDown) {
            reset()
            return
        }

        if (!canAttack()) {
            return
        }

        attack()
    }

    @SubscribeEvent
    fun onTick(e: TickEvent) {
        ticksDownRight = if (Mouse.isButtonDown(1)) ticksDownRight + 1 else 0
    }

    @SubscribeEvent
    fun onMouseOver(e: MouseOverEvent) {
        target?.let {
            e.movingObjectPosition = RaytraceUtil.getRotationOver(RotationComponent.yaw, RotationComponent.pitch)
        }
    }

    @SubscribeEvent
    fun onRender3D(e: Render3DEvent) {
        target?.let {
            GL11.glPushMatrix()
            GL11.glEnable(3042)
            GL11.glLineWidth(1.8f)
            GL11.glBlendFunc(770, 771)
            GL11.glEnable(2848)
            GlStateManager.depthMask(true)

            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glDepthMask(false)
            val partialTicks = mc.timer.renderPartialTicks
            val x = it.lastTickPosX + (it.posX - it.lastTickPosX) * partialTicks
            val y = it.lastTickPosY + (it.posY - it.lastTickPosY) * partialTicks
            val z = it.lastTickPosZ + (it.posZ - it.lastTickPosZ) * partialTicks
            val width = it.width / 1.15f
            val height = it.height + (if (it.isSneaking) -0.2f else 0.1f)

            val color = if (it.hurtTime != 0) Color.red else Color.WHITE
            RenderUtil.color(ColorUtil.withAlpha(color, 60))
            RenderUtil.drawBoundingBox(
                AxisAlignedBB(
                    x - width + .1,
                    y + height + .1,
                    z - width + .1,
                    x + width - .1,
                    y,
                    z + width - .1
                )
            )
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDepthMask(true)
            GL11.glDisable(GL11.GL_BLEND)

            GL11.glDisable(3042)
            GL11.glDisable(2848)
            GL11.glPopMatrix()
            RenderUtil.color(Color.WHITE)
        }
    }

    private fun reset() {
        if (blockHit) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, Mouse.isButtonDown(1))
            blockHit = false
        }
    }

    private fun canAttack(): Boolean {
        return mc.currentScreen == null
                && mc.thePlayer.heldItem?.item is ItemSword
                && (!requireMouseDown.isEnabled() || mc.gameSettings.keyBindAttack.isKeyDown)
    }

    private fun attack() {
        if (!stopwatch.finished(nextSwing))
            return

        nextSwing = (1000 / ThreadLocalRandom.current().nextInt(15, 25)).toLong()

        if (!mc.thePlayer.isUsingItem) {
            //PlayerUtil.sendClick(0, true)
            //mc.thePlayer.swingItem()

            target?.let {
                if (RaytraceUtil.isMouseOver(it, RotationComponent.yaw, RotationComponent.pitch, range.setting.toDouble()))
                    mc.playerController.attackEntity(mc.thePlayer, it)
                else {
                    ChatUtil.display("skip hit, ${RotationComponent.yaw}, ${RotationComponent.pitch}")
                }
            }
            mc.thePlayer.swingItem()
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

    private fun getTargets(): List<EntityLivingBase> {
        return mc.theWorld.loadedEntityList.filterIsInstance<EntityLivingBase>().filter { isValid(it) }
    }

    private fun isValid(entity: EntityLivingBase): Boolean {
        if (entity == mc.thePlayer
            || mc.thePlayer.getDistanceToEntity(entity) > 6.0
            || entity.health <= 0
            || !RotationUtil.isInFOV(entity, fov.setting.toFloat())
            || (teams.isEnabled() && PlayerUtil.sameTeam(entity))
            || Client.commandManager[Friend.javaClass].friends.contains(entity)
        )
            return false

        if (RotationUtil.calculatePerfectRangeToEntity(entity) > range.setting.toDouble())
            return false

        return when (entity) {
            is EntityPlayer -> {
                true
            }
            else -> false
        }
    }
}