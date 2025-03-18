package jp.client.ui

import jp.client.util.account.MicrosoftLogin.getRefreshToken
import jp.client.util.account.MicrosoftLogin.login
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiButtonLanguage
import net.minecraft.client.gui.GuiLanguage
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiSelectWorld
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Session
import net.optifine.CustomPanorama
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Project
import java.awt.Color
import java.io.IOException

class MainMenu : GuiScreen() {
    private var panoramaTimer = 0
    private lateinit var viewportTexture: DynamicTexture
    private val titlePanoramaPaths = arrayOf(
        ResourceLocation("setsuna/panorama/panorama_0.png"),
        ResourceLocation("setsuna/panorama/panorama_1.png"),
        ResourceLocation("setsuna/panorama/panorama_2.png"),
        ResourceLocation("setsuna/panorama/panorama_3.png"),
        ResourceLocation("setsuna/panorama/panorama_4.png"),
        ResourceLocation("setsuna/panorama/panorama_5.png")
    )
    private lateinit var backgroundTexture: ResourceLocation

    override fun updateScreen() {
        ++panoramaTimer
    }

    override fun initGui() {
        this.viewportTexture = DynamicTexture(256, 256)
        backgroundTexture = mc.textureManager.getDynamicTextureLocation("background", this.viewportTexture)

        val i = 24
        val j = height / 4 + 48

        this.addSingleplayerMultiplayerButtons(j, 24)

        buttonList.add(GuiButton(0, width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")))
        buttonList.add(GuiButton(4, width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")))
        buttonList.add(GuiButtonLanguage(5, width / 2 - 124, j + 72 + 12))
    }

    private fun addSingleplayerMultiplayerButtons(p_73969_1_: Int, p_73969_2_: Int) {
        buttonList.add(GuiButton(1, width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer")))
        buttonList.add(GuiButton(2, width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer")))
        buttonList.add(GuiButton(14, width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, I18n.format("menu.online")))
    }

    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton) {
        if (button.id == 0) {
            mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
        }
        if (button.id == 5) {
            mc.displayGuiScreen(GuiLanguage(this, mc.gameSettings, mc.languageManager))
        }
        if (button.id == 1) {
            mc.displayGuiScreen(GuiSelectWorld(this))
        }
        if (button.id == 2) {
            mc.displayGuiScreen(GuiMultiplayer(this))
        }
        if (button.id == 14) {
            getRefreshToken { refreshToken: String? ->
                if (refreshToken != null) {
                    val loginData = login(refreshToken)
                    println(loginData.username)
                    mc.session = Session(loginData.username, loginData.uuid, loginData.mcToken, "microsoft")
                }
            }
        }
        if (button.id == 4) {
            mc.shutdown()
        }
    }

    private fun drawPanorama(p_73970_1_: Int, p_73970_2_: Int, p_73970_3_: Float) {
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        GlStateManager.matrixMode(5889)
        GlStateManager.pushMatrix()
        GlStateManager.loadIdentity()
        Project.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f)
        GlStateManager.matrixMode(5888)
        GlStateManager.pushMatrix()
        GlStateManager.loadIdentity()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f)
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.disableCull()
        GlStateManager.depthMask(false)
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        val i = 8
        var j = 64
        val custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties()
        if (custompanoramaproperties != null) {
            j = custompanoramaproperties.blur1
        }
        for (k in 0 until j) {
            GlStateManager.pushMatrix()
            val f = ((k % i).toFloat() / i.toFloat() - 0.5f) / 64.0f
            val f1 = ((k / i).toFloat() / i.toFloat() - 0.5f) / 64.0f
            val f2 = 0.0f
            GlStateManager.translate(f, f1, f2)
            GlStateManager.rotate(
                MathHelper.sin((this.panoramaTimer.toFloat() + p_73970_3_) / 400.0f) * 25.0f + 20.0f,
                1.0f,
                0.0f,
                0.0f
            )
            GlStateManager.rotate(-(this.panoramaTimer.toFloat() + p_73970_3_) * 0.1f, 0.0f, 1.0f, 0.0f)
            for (l in 0..5) {
                GlStateManager.pushMatrix()
                if (l == 1) {
                    GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f)
                }
                if (l == 2) {
                    GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
                }
                if (l == 3) {
                    GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f)
                }
                if (l == 4) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f)
                }
                if (l == 5) {
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
                }
                var aresourcelocation = titlePanoramaPaths
                if (custompanoramaproperties != null) {
                    aresourcelocation = custompanoramaproperties.panoramaLocations
                }
                mc.textureManager.bindTexture(aresourcelocation[l])
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
                val i1 = 255 / (k + 1)
                val f3 = 0.0f
                worldrenderer.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, i1).endVertex()
                worldrenderer.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, i1).endVertex()
                worldrenderer.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, i1).endVertex()
                worldrenderer.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, i1).endVertex()
                tessellator.draw()
                GlStateManager.popMatrix()
            }
            GlStateManager.popMatrix()
            GlStateManager.colorMask(true, true, true, false)
        }
        worldrenderer.setTranslation(0.0, 0.0, 0.0)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.matrixMode(5889)
        GlStateManager.popMatrix()
        GlStateManager.matrixMode(5888)
        GlStateManager.popMatrix()
        GlStateManager.depthMask(true)
        GlStateManager.enableCull()
        GlStateManager.enableDepth()
    }

    private fun rotateAndBlurSkybox(p_73968_1_: Float) {
        mc.textureManager.bindTexture(this.backgroundTexture)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.colorMask(true, true, true, false)
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        GlStateManager.disableAlpha()
        val i = 3
        var j = 3
        val custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties()
        if (custompanoramaproperties != null) {
            j = custompanoramaproperties.blur2
        }
        for (k in 0 until j) {
            val f = 1.0f / (k + 1).toFloat()
            val l = width
            val i1 = height
            val f1 = (k - i / 2).toFloat() / 256.0f
            worldrenderer.pos(l.toDouble(), i1.toDouble(), zLevel.toDouble()).tex((0.0f + f1).toDouble(), 1.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
            worldrenderer.pos(l.toDouble(), 0.0, zLevel.toDouble()).tex((1.0f + f1).toDouble(), 1.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
            worldrenderer.pos(0.0, 0.0, zLevel.toDouble()).tex((1.0f + f1).toDouble(), 0.0).color(1.0f, 1.0f, 1.0f, f)
                .endVertex()
            worldrenderer.pos(0.0, i1.toDouble(), zLevel.toDouble()).tex((0.0f + f1).toDouble(), 0.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
        }
        tessellator.draw()
        GlStateManager.enableAlpha()
        GlStateManager.colorMask(true, true, true, true)
    }

    private fun renderSkybox(p_73971_1_: Int, p_73971_2_: Int, p_73971_3_: Float) {
        mc.framebuffer.unbindFramebuffer()
        GlStateManager.viewport(0, 0, 256, 256)
        drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_)
        rotateAndBlurSkybox(p_73971_3_)
        var i = 3
        val custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties()
        if (custompanoramaproperties != null) {
            i = custompanoramaproperties.blur3
        }
        for (j in 0 until i) {
            rotateAndBlurSkybox(p_73971_3_)
            rotateAndBlurSkybox(p_73971_3_)
        }
        mc.framebuffer.bindFramebuffer(true)
        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight)
        val f2 = if (width > height) 120.0f / width.toFloat() else 120.0f / height.toFloat()
        val f = height.toFloat() * f2 / 256.0f
        val f1 = width.toFloat() * f2 / 256.0f
        val k = width
        val l = height
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldrenderer.pos(0.0, l.toDouble(), zLevel.toDouble()).tex((0.5f - f).toDouble(), (0.5f + f1).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        worldrenderer.pos(k.toDouble(), l.toDouble(), zLevel.toDouble())
            .tex((0.5f - f).toDouble(), (0.5f - f1).toDouble()).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        worldrenderer.pos(k.toDouble(), 0.0, zLevel.toDouble()).tex((0.5f + f).toDouble(), (0.5f - f1).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        worldrenderer.pos(0.0, 0.0, zLevel.toDouble()).tex((0.5f + f).toDouble(), (0.5f + f1).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        tessellator.draw()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderSkybox(mouseX, mouseY, partialTicks)

        val sr = ScaledResolution(mc)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}