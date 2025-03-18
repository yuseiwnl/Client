package jp.client.util.render

import jp.client.util.InstanceAccess
import jp.client.util.math.MathUtil
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.MathHelper.clamp_int
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max
import kotlin.math.min


object ColorUtil : InstanceAccess {
    fun getColor(red: Int, green: Int, blue: Int, alpha: Int): Int {
        var color = clamp_int(alpha, 0, 255) shl 24
        color = color or (clamp_int(red, 0, 255) shl 16)
        color = color or (clamp_int(green, 0, 255) shl 8)
        color = color or clamp_int(blue, 0, 255)
        return color
    }

    fun getColoredHP(entity: EntityLivingBase, healthPoints: Float): EnumChatFormatting {
        val maxHealth: Float = entity.maxHealth
        if (healthPoints > maxHealth * 4 / 5) return EnumChatFormatting.DARK_GREEN
        if (healthPoints > maxHealth * 3 / 5) return EnumChatFormatting.GREEN
        if (healthPoints > maxHealth * 2 / 5) return EnumChatFormatting.YELLOW
        return if (healthPoints > maxHealth / 5) EnumChatFormatting.RED else EnumChatFormatting.DARK_RED
    }

    fun glColor(color: Color) {
        GL11.glColor4f(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f)
    }

    fun drawInterpolatedText(text: String, x: Float, y: Float) {
        var w = 0
        for (i in text.indices) {
            val character = text[i].toString()
            val color = interpolateColorsBackAndForth(15, i * 20, Color(255, 85, 85), Color(255, 255, 255))
            mc.fontRendererObj.drawStringWithShadow(character, x + w, y, color.rgb)
            w += mc.fontRendererObj.getStringWidth(character)
        }
    }

    fun interpolateColorsBackAndForth(speed: Int, index: Int, start: Color, end: Color): Color {
        var angle = ((System.currentTimeMillis() / speed + index) % 360)
        angle = (if (angle >= 180) 360 - angle else angle) * 2
        return interpolateColorC(
            start,
            end,
            angle / 360.0
        )
    }

    private fun interpolateColorC(color1: Color, color2: Color, amount: Double): Color {
        return Color(
            interpolateInt(color1.red, color2.red, min(1.0, max(0.0, amount))),
            interpolateInt(color1.green, color2.green, min(1.0, max(0.0, amount))),
            interpolateInt(color1.blue, color2.blue, min(1.0, max(0.0, amount))),
            interpolateInt(color1.alpha, color2.alpha, min(1.0, max(0.0, amount)))
        )
    }

    private fun interpolate(oldValue: Double, newValue: Double, interpolationValue: Double): Double {
        return (oldValue + (newValue - oldValue) * interpolationValue)
    }

    private fun interpolateInt(oldValue: Int, newValue: Int, interpolationValue: Double): Int {
        return interpolate(oldValue.toDouble(), newValue.toDouble(), interpolationValue).toInt()
    }

    fun withAlpha(color: Color, alpha: Int): Color {
        if (alpha == color.alpha) return color
        return Color(color.red, color.green, color.blue, MathUtil.clamp(0, 255, alpha))
    }
}