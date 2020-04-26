package com.deax.dev.kotlingradle.listeners

import com.deax.dev.kotlingradle.MainPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.File
import java.util.*

data class PlayerData(
    val id: UUID,
    val name: String
) {
    var multiplaceEnabled: Boolean
        get() {
            return config["multiplaceEnabled"] as Boolean
        }
        set(value) {
            config["multiplaceEnabled"] = value
        }
    var range: Int
        get() {
            return config["range"] as Int
        }
        set(value) {
            config["range"] = value
        }
    val file = File(plugin.playersDir, "$name.yml")
    var config = run {
        var created = false
        if (!file.exists()) {
            created = file.createNewFile();
        }
        val config = YamlConfiguration.loadConfiguration(file)
        if (created) {
            config["multiplaceEnabled"] = plugin.config["multiplaceEnabled"]
            config["range"] = plugin.config["range"]
        }
        config
    }

    companion object : Listener {
        val data = HashMap<UUID, PlayerData>()

        private val plugin: MainPlugin = MainPlugin.instance;

        operator fun get(id: UUID?) = data[id]
        operator fun get(p: Player) = PlayerData[p.uniqueId]!!

        @EventHandler
        public fun onJoin(e: PlayerJoinEvent) {
            PlayerData(e.player.uniqueId, e.player.name)
        }

        @EventHandler
        public fun onQuit(e: PlayerQuitEvent) {
            with(data[e.player.uniqueId]!!) {
                config.save(file)
            }
            data.remove(e.player.uniqueId)
        }
    }

    init {
        data[id] = this
    }
}