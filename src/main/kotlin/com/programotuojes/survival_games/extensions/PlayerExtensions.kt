package com.programotuojes.survival_games.extensions

import com.programotuojes.survival_games.data.Arena
import com.programotuojes.survival_games.data.PlayerData
import com.programotuojes.survival_games.data.PlayerStatus
import com.programotuojes.survival_games.data.PluginState
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

fun Player.reset() {
    this.gameMode = GameMode.SURVIVAL
    this.exp = 0f
    this.level = 0
    this.health = 20.0
    this.healthScale = 20.0
    this.foodLevel = 20
    this.saturation = 5f
    this.saturatedRegenRate = 10
    this.unsaturatedRegenRate = 80
    this.inventory.clear()
    this.activePotionEffects.forEach { this.removePotionEffect(it.type) }
    this.isFlying = false
}

fun Player.restore(previousData: PlayerData) {
    this.setStatus(null)
    this.teleport(previousData.location)
    this.gameMode = previousData.gameMode
    this.exp = previousData.exp
    this.level = previousData.level
    this.health = previousData.health
    this.healthScale = previousData.healthScale
    this.foodLevel = previousData.foodLevel
    this.saturation = previousData.saturation
    this.saturatedRegenRate = previousData.saturatedRegenRate
    this.unsaturatedRegenRate = previousData.unsaturatedRegenRate
    this.inventory.contents = previousData.inventory
    previousData.potionEffects.forEach { this.addPotionEffect(it) }
    this.isFlying = previousData.isFlying
}

fun Player.getArenaName(): String? {
    return this.getMetadata("SurvivalGames-Arena").firstOrNull()?.asString()
}

fun Player.setArenaName(name: String) {
    this.setMetadata("SurvivalGames-Arena", FixedMetadataValue(PluginState.plugin, name))
}

fun Player.getArena(): Arena? {
    val arenaName = this.getMetadata("SurvivalGames-Arena").firstOrNull()?.asString() ?: return null
    return PluginState.arenas[arenaName]
}

fun Player.getStatus(): PlayerStatus? {
    val status = this.getMetadata("SurvivalGames-Status").firstOrNull()?.value()
    return status as? PlayerStatus
}

fun Player.setStatus(status: PlayerStatus?) {
    if (status == null) {
        this.removeMetadata("SurvivalGames-Status", PluginState.plugin)
        return
    }

    this.setMetadata("SurvivalGames-Status", FixedMetadataValue(PluginState.plugin, status))
}
