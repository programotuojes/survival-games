package com.programotuojes.survival_games.commands

import com.programotuojes.survival_games.data.PluginState
import org.bukkit.command.CommandSender

class Finalize : BaseCommand {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val arenaName = args.getOrElse(1) {
            sender.sendMessage("Arena name must be provided")
            return false
        }

        val arena = PluginState.pendingArenas.getOrElse(arenaName) {
            sender.sendMessage("Arena $arenaName not found")
            return false
        }

        if (arena.spawnPoints.size < 2) {
            sender.sendMessage("There must be at least 2 spawn points")
            return false
        }

        PluginState.pendingArenas.remove(arenaName)
        PluginState.arenas[arenaName] = arena
        arena.addToConfig()

        sender.sendMessage("Arena $arenaName created")
        return true
    }

    override fun getSuggestions(args: Array<out String>): MutableList<String> {
        if (args.size == 2) {
            return PluginState.pendingArenas.keys
                .filter { it.startsWith(args[1], true) }
                .toMutableList()
        }

        return mutableListOf()
    }
}
