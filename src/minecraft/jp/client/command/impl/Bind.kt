package jp.client.command.impl

import jp.client.Client
import jp.client.command.Command
import jp.client.module.Module
import jp.client.util.chat.ChatUtil
import org.lwjgl.input.Keyboard
import java.util.*

object Bind : Command("Bind", "Binds a module to the given key", "bind <module> <key>", arrayOf("bind", "b")) {
    override fun execute(args: Array<String>) {
        if (args.size == 3) {
            val module: Module? = Client.moduleManager[args[1]]
            if (module == null) {
                ChatUtil.display("Invalid module.")
                return
            }
            val inputCharacter = args[2].uppercase(Locale.getDefault())
            val keyCode = Keyboard.getKeyIndex(inputCharacter)
            module.keyCode = keyCode
            ChatUtil.display("Bound " + module.name + " to " + Keyboard.getKeyName(keyCode) + ".")
        } else {
            error(syntax)
        }
    }
}