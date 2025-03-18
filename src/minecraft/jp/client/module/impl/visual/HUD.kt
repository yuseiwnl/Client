package jp.client.module.impl.visual

import de.florianmichael.vialoadingbase.ViaLoadingBase
import jp.client.Client
import jp.client.event.Render2DEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.module.setting.impl.ModeSetting
import jp.client.util.render.ColorUtil
import jp.client.util.render.RenderUtil
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.I18n
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

object HUD : Module("HUD", Category.VISUAL, Keyboard.KEY_H) {
    @SubscribeEvent
    fun onRender2D(e: Render2DEvent) {
        ColorUtil.drawInterpolatedText("Client", 2F, 2F)
        mc.fontRendererObj.drawStringWithShadow("Protocol: ${ViaLoadingBase.getInstance().targetVersion.name} XYZ: ${mc.thePlayer.position.x}, ${mc.thePlayer.position.y}, ${mc.thePlayer.position.z}", 2f, e.scaledResolution.scaledHeight - mc.fontRendererObj.FONT_HEIGHT - 1f, Color(170, 170, 170).rgb)

        drawActivePotionEffects(e)
        drawArmorStatus(e)

        var count = 0
        val modules = Client.moduleManager.sortedBy { module ->
            val pretty: StringBuilder = StringBuilder(module.name.replace("([A-Z])".toRegex(), " $1"))
            for (setting in module.settings) {
                if (setting is ModeSetting && setting.name == "Mode")
                    pretty.append(" ${setting.currentMode}")
            }
            mc.fontRendererObj.getStringWidth(pretty.toString()) }.reversed()
        for (module in modules) {
            if (!module.isToggled() || module.category == Category.VISUAL) continue

            val pretty: StringBuilder = StringBuilder(module.name.replace("([A-Z])".toRegex(), " $1"))
            for (setting in module.settings) {
                if (setting is ModeSetting && setting.name == "Mode")
                    pretty.append(" §7${setting.currentMode}")
            }
            val color = ColorUtil.interpolateColorsBackAndForth(15, count * 20, Color(236, 133, 209), Color(28, 167, 222))

            RenderUtil.rectangle(
                (e.scaledResolution.scaledWidth - mc.fontRendererObj.getStringWidth(pretty.toString())).toDouble(),
                (mc.fontRendererObj.FONT_HEIGHT * (count)).toDouble(),
                mc.fontRendererObj.getStringWidth(pretty.toString()).toDouble(),
                mc.fontRendererObj.FONT_HEIGHT.toDouble(), Color(0,0 ,0, 60))

            RenderUtil.rectangle(
                (e.scaledResolution.scaledWidth - 1).toDouble(),
                (0f + mc.fontRendererObj.FONT_HEIGHT * (count)).toDouble(),
                mc.fontRendererObj.getStringWidth(pretty.toString()).toDouble(),
                mc.fontRendererObj.FONT_HEIGHT.toDouble(), color)

            mc.fontRendererObj.drawStringWithShadow(
                pretty.toString(),
                e.scaledResolution.scaledWidth - 2f - mc.fontRendererObj.getStringWidth(pretty.toString()),
                1f + mc.fontRendererObj.FONT_HEIGHT * (count),
                color.rgb
            )
            count++
        }
    }

    private fun drawActivePotionEffects(e: Render2DEvent) {
        val i = 2
        val collection = mc.thePlayer.activePotionEffects
        val sr = e.scaledResolution
        var j = sr.scaledHeight / 2 - collection.size * 20 / 2
        if (!collection.isEmpty()) {
            GlStateManager.pushMatrix()
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.disableLighting()
            GlStateManager.enableBlend()
            val l = 20
            for (potionEffect in mc.thePlayer.activePotionEffects) {
                val potion = Potion.potionTypes[potionEffect.potionID]
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                mc.textureManager.bindTexture(ResourceLocation("textures/gui/container/inventory.png"))
                if (potion.hasStatusIcon()) {
                    val i1 = potion.statusIconIndex
                    drawTexturedModalRect(i + 6, j + 7, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18)
                }
                var s1: String = I18n.format(potion.name, arrayOfNulls<Any>(0))
                when (potionEffect.amplifier) {
                    1 -> {
                        s1 = s1 + " " + I18n.format("enchantment.level.2", arrayOfNulls<Any>(0))
                    }
                    2 -> {
                        s1 = s1 + " " + I18n.format("enchantment.level.3", arrayOfNulls<Any>(0))
                    }
                    3 -> {
                        s1 = s1 + " " + I18n.format("enchantment.level.4", arrayOfNulls<Any>(0))
                    }
                }
                mc.fontRendererObj.drawStringWithShadow(s1, (i + 10 + 18).toFloat(), (j + 6).toFloat(), 16777215)
                val s = Potion.getDurationString(potionEffect)
                mc.fontRendererObj.drawStringWithShadow(s, (i + 10 + 18).toFloat(), (j + 6 + 10).toFloat(), 16777215)
                j += l
            }
            GlStateManager.disableBlend()
            GlStateManager.popMatrix()
        }
    }

    private fun drawArmorStatus(e: Render2DEvent) {
        GlStateManager.pushMatrix()
        var x = e.scaledResolution.scaledWidth / 2 + 14
        val y = e.scaledResolution.scaledHeight - 56
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderHelper.enableGUIStandardItemLighting()
        mc.renderItem.zLevel = 200.0f
        for (i in 3 downTo 0) {
            val itemStack = mc.thePlayer.inventory.armorItemInSlot(i)
            mc.renderItem.renderItemAndEffectIntoGUI(itemStack, x, y)
            mc.renderItem.renderItemOverlays(mc.fontRendererObj, itemStack, x, y)
            x += 17
        }
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableBlend()
        mc.renderItem.zLevel = 0.0f
        GlStateManager.popMatrix()
    }

    fun drawTexturedModalRect(x: Int, y: Int, textureX: Int, textureY: Int, width: Int, height: Int) {
        val f = 0.00390625f
        val f1 = 0.00390625f
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer.pos(x.toDouble(), (y + height).toDouble(), 0.0)
            .tex((textureX * f).toDouble(), ((textureY + height) * f1).toDouble()).endVertex()
        worldrenderer.pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
            .tex(((textureX + width) * f).toDouble(), ((textureY + height) * f1).toDouble()).endVertex()
        worldrenderer.pos((x + width).toDouble(), y.toDouble(), 0.0)
            .tex(((textureX + width) * f).toDouble(), (textureY * f1).toDouble()).endVertex()
        worldrenderer.pos(x.toDouble(), y.toDouble(), 0.0).tex((textureX * f).toDouble(), (textureY * f1).toDouble())
            .endVertex()
        tessellator.draw()
    }
}