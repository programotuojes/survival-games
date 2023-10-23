package com.programotuojes.survival_games.commands

import com.programotuojes.survival_games.data.PluginState
import org.bukkit.command.CommandSender

class Reset : BaseCommand {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val arenaName = args.getOrElse(1) {
            sender.sendMessage("Arena name must be provided")
            return false
        }
        val arena = PluginState.arenas.getOrElse(arenaName) {
            sender.sendMessage("Arena $arenaName not found")
            return false
        }

        arena.reset()
        return true
    }

    override fun getSuggestions(args: Array<out String>): MutableList<String> {
        if (args.size == 2) {
            return PluginState.arenas.keys
                .filter { it.startsWith(args[1], true) }
                .toMutableList()
        }

        return mutableListOf()
    }
}
