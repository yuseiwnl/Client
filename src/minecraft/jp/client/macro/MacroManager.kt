package jp.client.macro

import jp.client.Client
import jp.client.event.KeyEvent
import jp.client.event.annotations.SubscribeEvent
import net.minecraft.client.Minecraft

class MacroManager : HashMap<Int, String>() {
    init {
        Client.eventBus.register(this)
    }
    @SubscribeEvent
    fun onKey(e: KeyEvent) {
        entries.stream().filter { it -> it.key == e.key }.forEach {
            if (it.value == "/ability") {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("Ability: ${Minecraft.getMinecraft().thePlayer.experienceLevel} (${Minecraft.getMinecraft().thePlayer.ticksExisted})")
            }
            else Minecraft.getMinecraft().thePlayer.sendChatMessage(it.value)
        }
    }
}