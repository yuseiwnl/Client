package jp.client.command.impl

import jp.client.Client
import jp.client.command.Command
import jp.client.util.chat.ChatUtil
import org.lwjgl.input.Keyboard

object Macro : Command("Macro", "Manage macros", "macro <add/remove/list> <text> <key>", arrayOf("macro", "m")) {
    override fun execute(args: Array<String>) {
        when(args[1]) {
            "add" -> {
                if (args[3].length != 1) {
                    error("Key is not valid.")
                }
                Client.macroManager.put(Keyboard.getKeyIndex(args[3].uppercase()), args[2])
                ChatUtil.display("Added macro ${args[2]} (§a${args[3].uppercase()}§f).")
            }

            "remove" -> {
                for (entry in Client.macroManager.entries) {
                    if (entry.value == args[2]) {
                        Client.macroManager.remove(entry.key)
                        ChatUtil.display("Removed macro.")
                    }
                }
            }

            "list" -> {
                ChatUtil.display("Macros:")
                for (entry in Client.macroManager.entries) {
                    ChatUtil.print("${entry.value} (§a${Keyboard.getKeyName(entry.key)}§f)")
                }
            }
        }
    }
}