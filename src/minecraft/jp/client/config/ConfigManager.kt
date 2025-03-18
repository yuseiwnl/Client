package jp.client.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import jp.client.Client
import jp.client.module.Module
import jp.client.module.setting.impl.BooleanSetting
import jp.client.module.setting.impl.ModeSetting
import jp.client.module.setting.impl.NumberSetting
import net.minecraft.client.Minecraft
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files

class ConfigManager {
    private val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()

    private fun saveConfig(name: String, content: String): Boolean {
        val localConfig = Config(name)
        localConfig.file.parentFile.mkdirs()
        try {
            Files.write(localConfig.file.toPath(), content.toByteArray())
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun saveConfig(name: String): Boolean {
        return saveConfig(name, serialize())
    }

    private fun serialize(): String {
        val jsonObject = JsonObject()
        for (module in Client.moduleManager) {
            val config = JsonObject()
            config.addProperty("toggled", module.isToggled())
            config.addProperty("keyCode", module.keyCode)

            for (setting in module.settings) {
                if (setting is BooleanSetting) {
                    config.addProperty(setting.name, setting.setting)
                }

                if (setting is ModeSetting) {
                    config.addProperty(setting.name, setting.currentMode)
                }

                if (setting is NumberSetting) {
                    config.addProperty(setting.name, setting.setting)
                }
            }

            jsonObject.add(module.name, config)
        }
        return gson.toJson(jsonObject)
    }

    fun loadConfig(data: String): Boolean {
        return loadConfig(data, true)
    }

    private fun loadConfig(data: String, keybinds: Boolean): Boolean {
        val fileReader = FileReader(File(Minecraft.getMinecraft().mcDataDir.toString() + "/client/configs/" + data + ".json"))
        val bufferedReader = BufferedReader(fileReader)
        val jsonObject: JsonObject = gson.fromJson(bufferedReader, JsonObject::class.java)

        bufferedReader.close()
        fileReader.close()

        for (module in Client.moduleManager) {
            if (!keybinds) {
                if (module.category == Module.Category.VISUAL) continue
            }

            if (!jsonObject.has(module.name)) {
                continue
            }

            if (module.isToggled())
                module.setToggled(false)

            val moduleJsonObject = jsonObject.getAsJsonObject(module.name)
            val state = moduleJsonObject["toggled"].asBoolean
            if (module.isToggled() != state)
                module.setToggled(state)

            // checks if key codes should be updated
            if (!keybinds) {
                continue
            }

            // checks if key codes of the module can be updated
            if (moduleJsonObject.has("keyCode")) {
                val keyCode = moduleJsonObject["keyCode"].asInt
                module.keyCode = keyCode
            }

            for (setting in module.settings) {
                if (moduleJsonObject.has(setting.name)) {
                    if (setting is BooleanSetting) {
                        setting.setting = moduleJsonObject[setting.name].asBoolean
                    }

                    if (setting is ModeSetting) {
                        setting.currentMode = moduleJsonObject[setting.name].asString
                    }

                    if (setting is NumberSetting) {
                        setting.setting = moduleJsonObject[setting.name].asNumber
                    }
                }
            }
        }
        return true
    }
}