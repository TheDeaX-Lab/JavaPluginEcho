package com.deax.dev.kotlingradle

import com.deax.dev.kotlingradle.commands.CompasCmd
// import com.deax.dev.kotlingradle.commands.EchoCmd
// import com.deax.dev.kotlingradle.listeners.MainListener
// import com.deax.dev.kotlingradle.listeners.PlayerData
// import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MainPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: MainPlugin;
    }

    override fun onEnable() {
        logger.info("Enabling...")
        instance = this;

        //getCommand("echo")!!.setExecutor(EchoCmd)
        getCommand("setcompas")!!.setExecutor(CompasCmd)

        //Bukkit.getPluginManager().registerEvents(MainListener, this)
        //Bukkit.getPluginManager().registerEvents(PlayerData, this)

        logger.info("Enabled!")
    }
}
