package jp.client.module.impl.visual

import jp.client.Client
import jp.client.event.Render2DEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.module.impl.combat.KillAura
import jp.client.util.render.RenderUtil
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.gui.Gui
import net.minecraft.util.EnumChatFormatting
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.String
import kotlin.Float
import kotlin.let

object TargetHUD : Module("TargetHUD", Category.VISUAL) {
    const val X = 400
    const val Y = 400
    var currentHealthBarFillWidth = 0f

    @SubscribeEvent
    fun onRender2D(e: Render2DEvent) {
        Client.moduleManager[KillAura.javaClass].target?.let {
            val nameWidth = mc.fontRendererObj.getStringWidth(it.displayName.formattedText)
            val width = 80.coerceAtLeast(nameWidth + 20)
            val height = 25

            RenderUtil.rectangle(X - 3.0, Y - 3.0, width + height + 6.0, height + 6.0, Color(0, 0, 0, 60))
            val resourceLocation = (it as AbstractClientPlayer).locationSkin
            mc.fontRendererObj.drawStringWithShadow(it.displayName.formattedText, X + height + 3f, Y + 1f, -1)
            val healthText = String.format("%.1f", it.health) + EnumChatFormatting.RED + " \u2764"
            mc.fontRendererObj.drawStringWithShadow(healthText, X + height + 3f,  Y + 11f, -1)

            val healthBarWidth = width - 3
            val percentage = it.health / it.maxHealth
            val currentHealthBarWidth = percentage * healthBarWidth
            currentHealthBarFillWidth = lerp(currentHealthBarFillWidth, currentHealthBarWidth, 0.1f)

            RenderUtil.rectangle(X + height + 3.0, Y + 21.0, healthBarWidth.toDouble(), 3.0, Color(64, 64, 64))
            RenderUtil.rectangle(X + height + 3.0, Y + 21.0, currentHealthBarFillWidth.toDouble(), 3.0, Color(255, 255, 255))

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
            mc.textureManager.bindTexture(resourceLocation)
            Gui.drawScaledCustomSizeModalRect(X, Y, 4f, 4f, 4, 4, 25, 25, 32f, 32f)
        }
    }

    fun lerp(start: Float, end: Float, speed: Float): Float {
        return start + (end - start) * speed
    }
}