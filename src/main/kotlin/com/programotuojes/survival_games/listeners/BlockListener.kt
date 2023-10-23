package com.programotuojes.survival_games.listeners

import com.programotuojes.survival_games.extensions.getStatus
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent

class BlockListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.player.getStatus() != null) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockDamage(event: BlockDamageEvent) {
        if (event.player.getStatus() != null) {
            event.isCancelled = true
        }
    }
}
