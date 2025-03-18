package jp.client.command.impl

import jp.client.Client
import jp.client.command.Command
import jp.client.util.chat.ChatUtil
import net.minecraft.util.EnumChatFormatting

object Config : Command("Config", "Toggles a module", "config <load/save> <name>", arrayOf("config", "c")) {
    override fun execute(args: Array<String>) {
        val config = args[2]
        when (args[1]) {
            "load" -> {
                Client.configManager.loadConfig(config)
                ChatUtil.display("Loaded config (" + EnumChatFormatting.GREEN + EnumChatFormatting.ITALIC + "$config.json" + EnumChatFormatting.RESET + ")")
            }

            "save" -> {
                Client.configManager.saveConfig(args[2])
                ChatUtil.display("Saved config (" + EnumChatFormatting.GREEN + EnumChatFormatting.ITALIC + "$config.json" + EnumChatFormatting.RESET + ")")
            }

            else -> error(this.syntax)
        }
    }

}