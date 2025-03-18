package jp.client.command

import jp.client.util.InstanceAccess
import jp.client.util.chat.ChatUtil

abstract class Command(val name: String, val description: String, val syntax: String, val aliases: Array<String>) : InstanceAccess {
    abstract fun execute(args: Array<String>)

    private fun error() {
        ChatUtil.display("Invalid command arguments.")
    }

    protected fun error(usage: String) {
        error()
        ChatUtil.display("Correct Usage: $usage")
    }
}