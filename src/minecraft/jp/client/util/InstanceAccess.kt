package jp.client.util

import net.minecraft.client.Minecraft

interface InstanceAccess {
    val mc: Minecraft
        get() = Minecraft.getMinecraft()
}