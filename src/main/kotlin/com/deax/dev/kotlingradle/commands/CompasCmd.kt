package com.deax.dev.kotlingradle.commands

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CompasCmd : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when (sender) {
            is Player -> {
                with(sender.player!!) {
                    if (args.size == 2) {
                        try {
                            val x = args[0].toDouble()
                            val z = args[1].toDouble()
                            compassTarget = Location(sender.world, x, 0.0, z)
                            sendMessage(ChatColor.GREEN.toString() + "Compass set coordinates $x $z")
                        } catch (e: NumberFormatException) {
                            return false
                        }
                    } else if (args.size == 1) {
                        if (args[0] == "clear") {
                            compassTarget = world.spawnLocation
                            sendMessage(ChatColor.GREEN.toString() + "Compass cleared!")
                        } else {
                            return false
                        }
                    } else {
                        return false
                    }
                }
            }
            else -> sender.sendMessage(ChatColor.RED.toString() + "Command only for player");
        }
        return true;
    }
}