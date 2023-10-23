package com.programotuojes.survival_games.extensions

import com.programotuojes.survival_games.data.PluginState
import org.bukkit.block.Block
import org.bukkit.metadata.FixedMetadataValue

fun Block.getLastRefillTime(): Long? {
    return this.getMetadata("SurvivalGames-LastRefilled").firstOrNull()?.asLong()
}

fun Block.setLastRefillTime() {
    this.setMetadata("SurvivalGames-LastRefilled", FixedMetadataValue(PluginState.plugin, System.currentTimeMillis()))
}
