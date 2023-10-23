package com.programotuojes.survival_games.listeners

import com.programotuojes.survival_games.data.PlayerStatus
import com.programotuojes.survival_games.extensions.getStatus
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class MoveListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onMove(event: PlayerMoveEvent) {
        val status = event.player.getStatus() ?: return

        if (status == PlayerStatus.JOINED || status == PlayerStatus.READY) {
            event.to.x = event.from.x
            event.to.y = event.from.y
            event.to.z = event.from.z
        }
    }
}
