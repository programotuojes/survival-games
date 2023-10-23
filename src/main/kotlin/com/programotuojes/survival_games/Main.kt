package com.programotuojes.survival_games

import com.programotuojes.survival_games.commands.BaseHandler
import com.programotuojes.survival_games.data.Arena
import com.programotuojes.survival_games.data.PluginState
import com.programotuojes.survival_games.listeners.*
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Main : JavaPlugin() {

    override fun onEnable() {
        PluginState.plugin = this

        saveDefaultConfig()
        Arena.loadArenas()

        getCommand("sg")!!.setExecutor(BaseHandler())

        Bukkit.getPluginManager().registerEvents(BlockListener(), this)
        Bukkit.getPluginManager().registerEvents(ChestListener(), this)
        Bukkit.getPluginManager().registerEvents(MoveListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerListener(), this)
    }
}
