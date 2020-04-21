package com.deax.dev.kotlingradle.listeners

import com.deax.dev.kotlingradle.MainPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

data class PlayerData(val id: UUID, val name: String) {
    var silly: Boolean = false;

    companion object : Listener {
        private val data = HashMap<UUID, PlayerData>()

        private val plugin: MainPlugin = MainPlugin.instance;

        operator fun get(id: UUID?) = data[id]
        operator fun get(p: Player) = PlayerData[p.uniqueId]!!

        @EventHandler
        public fun onJoin(e: PlayerJoinEvent) {
            PlayerData(e.player.uniqueId, e.player.name)
            plugin.logger.info("Player ${e.player.name} joined to server")
        }

        @EventHandler
        public fun onQuit(e: PlayerQuitEvent) {
            data.remove(e.player.uniqueId)
            plugin.logger.info("Player ${e.player.name} exited from server")
        }
    }

    init {
        data[id] = this
    }
}