package com.programotuojes.survival_games.listeners

import com.programotuojes.survival_games.data.PlayerStatus
import com.programotuojes.survival_games.extensions.getArena
import com.programotuojes.survival_games.extensions.getStatus
import com.programotuojes.survival_games.extensions.setStatus
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player

        if (player.getStatus() != PlayerStatus.PLAYING) {
            return
        }

        for (itemStack in player.inventory.contents.filterNotNull()) {
            player.world.dropItemNaturally(player.location, itemStack)
        }

        val arena = player.getArena() ?: return
        val otherPlayers = arena.players.filter { it.key != player.uniqueId }

        val killer = player.killer
        val message = if (killer != null)
            "${player.name} was killed by ${killer.name}"
        else
            "${player.name} died of stupidity"

        otherPlayers.values.forEach { it.sendMessage(message) }

        player.world.playSound(player.location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 6f, 0.5f)

        player.gameMode = GameMode.SPECTATOR
        player.setStatus(PlayerStatus.SPECTATING)
        player.inventory.clear()

        event.isCancelled = true

        // TODO doesn't work. 2nd last player isn't reset
        arena.stopIfLastPlayer()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDamage(event: EntityDamageEvent) {
        val player = event.entity
        if (player !is Player) return

        if (player.getStatus() != PlayerStatus.PLAYING) {
            return
        }

        val arena = player.getArena() ?: return
        if (!arena.allowDamage) {
            event.isCancelled = true
        }
    }
}
