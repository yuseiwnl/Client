package jp.client.util.chat

import jp.client.util.InstanceAccess
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting

object ChatUtil : InstanceAccess {
    fun display(message: Any, vararg objects: Any?) {
        if (mc.thePlayer != null) {
            val format = String.format(message.toString(), *objects)
            mc.thePlayer.addChatMessage(ChatComponentText(getPrefix() + format))
        }
    }

    fun print(message: Any, vararg objects: Any?) {
        if (mc.thePlayer != null) {
            val format = String.format(message.toString(), *objects)
            mc.thePlayer.addChatMessage(ChatComponentText(EnumChatFormatting.GRAY.toString() + "» " + EnumChatFormatting.RESET + format))
        }
    }

    private fun getPrefix(): String {
        val color = EnumChatFormatting.RED.toString()
        return color + EnumChatFormatting.BOLD + "Setsuna" + EnumChatFormatting.RESET + EnumChatFormatting.GRAY + " » " + EnumChatFormatting.RESET
    }
}