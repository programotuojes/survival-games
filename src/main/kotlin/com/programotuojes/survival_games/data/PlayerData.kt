package com.programotuojes.survival_games.data

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect

class PlayerData(player: Player) {
    val location = player.location
    val gameMode = player.gameMode
    val exp = player.exp
    val level = player.level
    val health = player.health
    val healthScale = player.healthScale
    val foodLevel = player.foodLevel
    val saturation = player.saturation
    val saturatedRegenRate = player.saturatedRegenRate
    val unsaturatedRegenRate = player.unsaturatedRegenRate
    val inventory: Array<ItemStack?> = player.inventory.contents
    val potionEffects: Collection<PotionEffect> = player.activePotionEffects
    val isFlying = player.isFlying
}
