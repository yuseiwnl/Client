package jp.client.module.impl.visual

import jp.client.event.RenderLivingEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_FILL

object Chams : Module("Chams", Category.VISUAL, Keyboard.KEY_F) {
    @SubscribeEvent
    fun onRenderLiving(e: RenderLivingEvent) {
        if (e.state == RenderLivingEvent.State.PRE) {
            if (e.entity is EntityPlayer) {
                GL11.glEnable(GL_POLYGON_OFFSET_FILL)
                GL11.glPolygonOffset(1.0f, -1100000F)
            }
        } else {
            if (e.entity is EntityPlayer) {
                GL11.glDisable(GL_POLYGON_OFFSET_FILL)
                GL11.glPolygonOffset(1.0f, 1100000F)
            }
        }
    }
}