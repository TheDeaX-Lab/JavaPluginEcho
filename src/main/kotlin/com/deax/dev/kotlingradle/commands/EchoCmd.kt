package com.deax.dev.kotlingradle.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object EchoCmd : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<out String>): Boolean {
        sender.sendMessage(args.joinToString(separator = " "))
        return true
    }
}