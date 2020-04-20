package com.deax.dev.kotlingradle

import com.deax.dev.kotlingradle.commands.EchoCmd
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.java.JavaPlugin

class MainPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("Enabling...")

        getCommand("echo")!!.setExecutor(EchoCmd)

        logger.info("Config Val: ${config.getString("configVal") ?: "[no val listed]"}")

        logger.info("Enabled!")
    }
}
