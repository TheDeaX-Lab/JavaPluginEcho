package com.deax.dev.kotlingradle

import com.deax.dev.kotlingradle.commands.MultiFarmCmd
import com.deax.dev.kotlingradle.listeners.MainListener
import com.deax.dev.kotlingradle.listeners.PlayerData
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class MainPlugin : JavaPlugin() {
    val playersDir = File(dataFolder, "players" + File.separator)
    val configFile = File(dataFolder, "config.yml")

    companion object {
        lateinit var instance: MainPlugin;
    }

    override fun onEnable() {
        instance = this

        if (!configFile.exists()) {
            config.options().copyDefaults(true)
            saveDefaultConfig()
        }
        config.load(configFile)

        logger.info("Enabling...")
        if (config["isEnabled"] as Boolean) {
            if (!playersDir.exists()) {
                playersDir.mkdirs()
            }
            server.onlinePlayers.forEach {
                PlayerData(it.uniqueId, it.name)
            }
            getCommand("multifarm")!!.setExecutor(MultiFarmCmd)
            Bukkit.getPluginManager().registerEvents(MainListener, this)
            Bukkit.getPluginManager().registerEvents(PlayerData, this)
        }

        // getCommand("echo")!!.setExecutor(EchoCmd)
        // getCommand("setcompas")!!.setExecutor(CompasCmd)

        logger.info("Enabled!")
    }

    override fun onDisable() {
        logger.info("Saving configs...")
        if (config["isEnabled"] as Boolean) {
            PlayerData.data.forEach {
                val (_, player) = it;
                with(player) {
                    config.save(file)
                }
            }
        }
        logger.info("Plugin disabled!")
    }
}
