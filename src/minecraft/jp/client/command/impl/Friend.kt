package jp.client.command.impl

import jp.client.command.Command
import jp.client.util.chat.ChatUtil
import net.minecraft.entity.player.EntityPlayer

object Friend : Command("Friend", "Manage friends", "friend <add/remove> <player>", arrayOf("friend", "f")) {
    val friends = ArrayList<EntityPlayer>()

    override fun execute(args: Array<String>) {
        if (args.size != 3) {
            error(".f <add/remove> <player>")
        } else {
            val action = args[1].lowercase()
            val target = args[2]
            var success = false
            val iterator: Iterator<EntityPlayer> = mc.theWorld.playerEntities.iterator()
            while (true) {
                if (iterator.hasNext()) {
                    val entityPlayer = iterator.next()
                    if (!entityPlayer.name.equals(target, true))
                        continue
                    when (action) {
                        "add" -> {
                            friends.add(entityPlayer)
                            ChatUtil.display(String.format("Added %s to friends list", target))
                            success = true
                        }

                        "remove" -> {
                            friends.remove(entityPlayer)
                            ChatUtil.display(String.format("Removed %s from friends list"))
                            success = true
                        }
                    }
                } else {
                    break
                }
                if (!success) ChatUtil.display("That user could not be found.")
            }
            if (!success) ChatUtil.display("That user could not be found.")
        }
    }
}