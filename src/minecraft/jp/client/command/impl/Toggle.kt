package jp.client.command.impl

import jp.client.Client
import jp.client.command.Command
import jp.client.module.Module
import jp.client.util.chat.ChatUtil
import net.minecraft.util.EnumChatFormatting

object Toggle : Command("Toggle", "Toggles a module", "toggle <module>", arrayOf("toggle", "t")) {
    override fun execute(args: Array<String>) {
        if (args.size != 2) {
            error(String.format(".%s <module>", args[0]))
            return
        }
        val module: Module? = Client.moduleManager[args[1]]
        if (module == null) {
            ChatUtil.display("Invalid module.")
            return
        }

        module.toggle()
        ChatUtil.display("Toggled " + module.name + " " + if (module.isToggled()) EnumChatFormatting.GREEN.toString() + "on" else EnumChatFormatting.RED.toString() + "off")
    }
}