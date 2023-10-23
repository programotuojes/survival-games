package com.programotuojes.survival_games.commands

import com.programotuojes.survival_games.data.PluginState
import org.bukkit.command.CommandSender

class Cancel : BaseCommand {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.size != 2) {
            sender.sendMessage("Arena name must be provided")
            return false
        }

        val arenaName = args[1]

        val prevArena = PluginState.pendingArenas.remove(arenaName)
        if (prevArena == null) {
            sender.sendMessage("Arena \"$arenaName\" not found")
            return false
        }

        sender.sendMessage("Arena setup cancelled")
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
