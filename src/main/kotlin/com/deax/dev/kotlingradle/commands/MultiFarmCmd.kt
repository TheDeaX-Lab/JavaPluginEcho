package com.deax.dev.kotlingradle.commands

import com.deax.dev.kotlingradle.MainPlugin
import com.deax.dev.kotlingradle.listeners.PlayerData
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.Exception


object MultiFarmCmd : CommandExecutor {
    val plugin = MainPlugin.instance

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when (sender) {
            is Player -> {
                with(sender.player!!) {
                    val pdata = PlayerData[uniqueId]!!
                    when (args.size) {
                        1 -> {
                            when (args[0]) {
                                "clear" -> {
                                    pdata.range = plugin.config["range"] as Int
                                    pdata.multiplaceEnabled = plugin.config["multiplaceEnabled"] as Boolean
                                    sender.sendMessage(ChatColor.GREEN.toString() + "Cleared to default settings")
                                }
                                "enable" -> {
                                    pdata.multiplaceEnabled = true
                                    sender.sendMessage(ChatColor.GREEN.toString() + "Multifarm enabled!")
                                }
                                "disable" -> {
                                    pdata.multiplaceEnabled = false
                                    sender.sendMessage(ChatColor.GREEN.toString() + "Multifarm disabled!")
                                }
                                else -> return false
                            }
                        }
                        2 -> {
                            when (args[0]) {
                                "range" -> {
                                    try {
                                        val range = args[1].toInt()
                                        val maxRange = plugin.config["maxRange"] as Int
                                        if (range > 0) {
                                            if (range > maxRange) {
                                                sender.sendMessage(ChatColor.RED.toString() + "Radius $maxRange is max!")
                                            } else {
                                                pdata.range = range;
                                                sender.sendMessage(ChatColor.GREEN.toString() + "Radius is set!")
                                            }
                                        } else {
                                            sender.sendMessage(ChatColor.RED.toString() + "Range need positive number")
                                        }
                                    } catch (ex: Exception) {
                                        return false
                                    }
                                }
                                else -> return false
                            }
                        }
                        else -> return false
                    }
                }
            }
            else -> sender.sendMessage(ChatColor.RED.toString() + "Only for players")
        }
        return true
    }
}