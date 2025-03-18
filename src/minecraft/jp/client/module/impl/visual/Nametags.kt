package jp.client.module.impl.visual

import jp.client.component.impl.ProjectionComponent
import jp.client.event.Render2DEvent
import jp.client.event.RenderNameEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.util.render.ColorUtil
import jp.client.util.render.RenderUtil
import net.minecraft.client.gui.FontRenderer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.input.Keyboard
import java.awt.Color
import kotlin.math.roundToInt

object Nametags : Module("Nametags", Category.VISUAL, Keyboard.KEY_F) {
    @SubscribeEvent
    fun onRenderName(e: RenderNameEvent) {
        val entity = e.entity
        if (entity !is EntityPlayer || entity == mc.thePlayer)
            return

        e.canceled = true
    }

    @SubscribeEvent
    fun onRender2D(e: Render2DEvent) {
        for (entity in mc.theWorld.loadedEntityList.sortedBy { entity -> mc.thePlayer.getDistanceToEntity(entity) }.reversed()) {
            if (entity !is EntityPlayer || entity == mc.thePlayer || !RenderUtil.isInViewFrustum(entity))
                continue

            val distance = "§7" + entity.getDistanceToEntity(mc.thePlayer).roundToInt() + "m§f "
            val health = " " + ColorUtil.getColoredHP(
                entity as EntityLivingBase,
                (entity as EntityLivingBase).health.roundToInt().toFloat()
            ) + (entity as EntityLivingBase).health.roundToInt()
            val str = distance + entity.getDisplayName().formattedText + health
            val x =
                entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.viewerPosX
            val y =
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.viewerPosY
            val z =
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.viewerPosZ

            val fontRenderer: FontRenderer = mc.fontRendererObj
            val f = 1.6f
            val f1 = 0.02666667f //0.016666668f * f

            val position = ProjectionComponent.get(entity)
            if (position == null)
                continue

            val posX = (position.x + (position.z - position.x) / 2)
            val posY = position.y - mc.fontRendererObj.FONT_HEIGHT - 2
            val width = fontRenderer.getStringWidth(str)

            RenderUtil.rectangle(posX - width / 2 - 1, posY - 1, fontRenderer.getStringWidth(str) + 1.0, fontRenderer.FONT_HEIGHT.toDouble() + 1, Color(0, 0, 0, 64))
            fontRenderer.drawStringWithShadow(str, (posX - width / 2).toFloat(), posY.toFloat(), -1)

            /*
            GlStateManager.pushMatrix()
            //GlStateManager.translate(x.toFloat() + 0.0f, y.toFloat() + entity.height + 0.5f, z.toFloat())
            val j = fontRenderer.getStringWidth(str) / 2
            GlStateManager.translate((position.x + ((position.z - position.x) / 2)) - j, position.y - mc.fontRendererObj.FONT_HEIGHT - 2, 0.0)
            GL11.glNormal3f(0.0f, 1.0f, 0.0f)
            /*
            GlStateManager.rotate(-mc.renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
            if (mc.gameSettings.thirdPersonView == 2) {
                GlStateManager.rotate(-mc.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
            } else {
                GlStateManager.rotate(mc.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
            }

             */
            //GlStateManager.scale(-f1, -f1, f1)
            //GlStateManager.disableLighting()
            //GlStateManager.depthMask(false)
            GlStateManager.disableDepth()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            val tessellator = Tessellator.getInstance()
            val worldRenderer = tessellator.worldRenderer
            val i = 0

            /*
            var scale1 = sqrt(x * x + y * y + z * z).toFloat()
            scale1 = (scale1 / 10.0f).coerceAtLeast(1.0f)
            scale1 *= 1.0f
            GlStateManager.translate(0.0f, -(scale1), 0.0f)
            GlStateManager.scale(scale1, scale1, scale1)

             */

            GlStateManager.disableTexture2D()
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR)
            worldRenderer.pos(-1.0, (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldRenderer.pos(-1.0, (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldRenderer.pos((j * 2 + 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldRenderer.pos((j * 2 + 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            tessellator.draw()
            GlStateManager.enableTexture2D()
            fontRenderer.drawStringWithShadow(str, 0F, 0F, -1)
            GlStateManager.enableDepth()
            //GlStateManager.depthMask(true)
            //GlStateManager.enableLighting()
            GlStateManager.disableBlend()
            //  GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.popMatrix()
             */
        }
    }
}