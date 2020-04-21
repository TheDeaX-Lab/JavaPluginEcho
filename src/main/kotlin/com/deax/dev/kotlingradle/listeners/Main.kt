package com.deax.dev.kotlingradle.listeners

import com.deax.dev.kotlingradle.MainPlugin
import org.bukkit.Location
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.EventHandler
import kotlin.math.sqrt

object MainListener : Listener {
    private val plugin: MainPlugin = MainPlugin.instance;

    // compare 2 locations
    infix fun Location.equalsBlock(other: Location) =
        this.blockX == other.blockX && this.blockY == other.blockY && this.blockZ == other.blockZ

    @EventHandler
    public fun onMove(e: PlayerMoveEvent) {
        // Damage players for 10k blocks radius
        if (!(e.from equalsBlock e.to!!)) {
            val r = with(e.to!!.block) {
                sqrt((x * x + z * z).toDouble())
            }

            if (r > 10000.0) {
                e.player.damage(1.0);
                e.player.sendMessage("You reached 10k radius!!!");
            }
        }
    }
}
