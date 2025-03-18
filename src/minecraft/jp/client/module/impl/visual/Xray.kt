package jp.client.module.impl.visual

import jp.client.module.Module
import org.lwjgl.input.Keyboard

object Xray : Module("Xray", Category.VISUAL, Keyboard.KEY_X) {
    override fun onEnable() {
        super.onEnable()
        mc.renderGlobal.loadRenderers()
    }

    override fun onDisable() {
        mc.renderGlobal.loadRenderers()
        super.onDisable()
    }
}