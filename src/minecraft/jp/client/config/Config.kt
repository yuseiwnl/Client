package jp.client.config

import net.minecraft.client.Minecraft
import java.io.File

class Config(val name: String, val file: File) {
    constructor(name: String) : this(name, File(Minecraft.getMinecraft().mcDataDir.toString() + "/setsuna/configs/" + name + ".json"))
}