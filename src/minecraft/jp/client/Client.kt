package jp.client

import de.florianmichael.viamcp.ViaMCP
import jp.client.command.CommandManager
import jp.client.component.ComponentManager
import jp.client.config.ConfigManager
import jp.client.event.EventBus
import jp.client.macro.MacroManager
import jp.client.module.ModuleManager
import jp.client.util.packet.PacketManager
import org.lwjgl.opengl.Display

object Client {
    lateinit var commandManager: CommandManager
    lateinit var componentManager: ComponentManager
    lateinit var configManager: ConfigManager
    lateinit var eventBus: EventBus
    lateinit var macroManager: MacroManager
    lateinit var moduleManager: ModuleManager
    lateinit var packetManager: PacketManager

    fun init() {
        Display.setTitle("Client 4.0")

        try {
            ViaMCP.create()

            // In case you want a version slider like in the Minecraft options, you can use this code here, please choose one of those:

            ViaMCP.INSTANCE.initAsyncSlider() // For top left aligned slider
            //ViaMCP.INSTANCE.initAsyncSlider(x, y, width (min. 110), height (recommended 20)); // For custom position and size slider
        } catch (e: Exception) {
            e.printStackTrace()
        }

        eventBus = EventBus()

        commandManager = CommandManager()
        componentManager = ComponentManager()
        configManager = ConfigManager()
        macroManager = MacroManager()
        moduleManager = ModuleManager()
        packetManager = PacketManager()
    }
}