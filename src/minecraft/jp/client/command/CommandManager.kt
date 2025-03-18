package jp.client.command

import jp.client.Client
import jp.client.command.impl.*
import jp.client.event.ChatEvent
import jp.client.event.annotations.SubscribeEvent
import jp.client.module.Module
import jp.client.module.setting.Setting
import jp.client.module.setting.impl.BooleanSetting
import jp.client.module.setting.impl.ModeSetting
import jp.client.module.setting.impl.NumberSetting
import jp.client.util.chat.ChatUtil
import net.minecraft.util.EnumChatFormatting
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList

class CommandManager : ArrayList<Command>() {
    init {
        this.addAll(listOf(
            Bind,
            Config,
            Friend,
            Macro,
            Toggle
        ))

        Client.eventBus.register(this)
    }

    @SubscribeEvent
    fun onChat(e: ChatEvent) {
        var message = e.message
        if (!message.startsWith(".")) return
        message = message.substring(1)
        val args: Array<String> = message.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val commandFound = AtomicBoolean(false)
        try {
            stream().filter { command: Command ->
                Arrays.stream(command.aliases).anyMatch { aliases: String ->
                    aliases.equals(args[0], ignoreCase = true)
                }
            }.forEach { command: Command ->
                commandFound.set(true)
                command.execute(args)
            }

            Client.moduleManager.stream().filter { module: Module ->
                module.name.equals(args[0], ignoreCase = true)
            }.forEach { module: Module ->
                when (args.size) {
                    1 -> {
                        ChatUtil.display(module.name + " (" + (if (module.isToggled()) EnumChatFormatting.GREEN.toString() + "ON" else EnumChatFormatting.RED.toString() + "OFF") + EnumChatFormatting.RESET +"):")
                        for (setting in module.settings) {
                            if (setting is BooleanSetting) {
                                ChatUtil.print(setting.name + ": " + if (setting.isEnabled()) EnumChatFormatting.GREEN.toString() + "true" else EnumChatFormatting.RED.toString() + "false")
                            }

                            if (setting is ModeSetting) {
                                ChatUtil.print(setting.name + ": " + EnumChatFormatting.BLUE + setting.currentMode.uppercase())
                            }

                            if (setting is NumberSetting) {
                                ChatUtil.print(setting.name + ": " + EnumChatFormatting.BLUE + setting.setting)
                            }
                        }
                        commandFound.set(true)
                    }

                    3 -> {
                        module.settings.stream().filter { setting: Setting<*> ->
                            setting.name.replace("\\s+".toRegex(), "").equals(args[1], ignoreCase = true)
                        }.forEach { setting: Setting<*> ->
                            if (setting is BooleanSetting) {
                                try {
                                    val boolean = args[2].toBoolean()
                                    ChatUtil.display(setting.name + " is now " + boolean + " (before set to " + setting.setting + ")")
                                    setting.setting = boolean
                                } catch (ex: Exception) {
                                    throw RuntimeException(ex)
                                }
                            }

                            if (setting is ModeSetting) {
                                try {
                                    val mode = args[2].replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                    if (setting.modes.contains(mode)) {
                                        ChatUtil.display(setting.name + " is now " + mode + " (before set to " + setting.currentMode + ")")
                                        setting.currentMode = mode
                                    } else {
                                        ChatUtil.display("$mode does not exist!")
                                    }
                                } catch (ex: Exception) {
                                    throw RuntimeException(ex)
                                }
                            }

                            if (setting is NumberSetting) {
                                try {
                                    val number = args[2].toDouble()
                                    ChatUtil.display(setting.name + " is now " + number + " (before set to " + setting.setting + ")")
                                    setting.setting = number
                                } catch (ex: Exception) {
                                    throw RuntimeException(ex)
                                }
                            }

                            commandFound.set(true)
                        }
                    }
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        if (!commandFound.get()) ChatUtil.display("Unknown command! Try .help if you're lost")
        e.canceled = true
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Command> get(command: Class<T>): T {
        return stream()
            .filter { cmd: Command -> cmd.javaClass == command }
            .findAny().orElse(null) as T
    }
}