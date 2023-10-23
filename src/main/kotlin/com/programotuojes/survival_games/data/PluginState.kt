package com.programotuojes.survival_games.data

import org.bukkit.plugin.Plugin
import java.util.UUID

object PluginState {
    lateinit var plugin: Plugin
    val config get() = plugin.config
    val arenas = hashMapOf<String, Arena>()
    val pendingArenas = hashMapOf<String, Arena>()
    val previousPlayerData = hashMapOf<UUID, PlayerData>()
}
