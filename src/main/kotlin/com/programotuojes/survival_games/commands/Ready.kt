package com.programotuojes.survival_games.commands

import com.programotuojes.survival_games.data.PlayerStatus
import com.programotuojes.survival_games.data.PluginState
import com.programotuojes.survival_games.extensions.getArenaName
import com.programotuojes.survival_games.extensions.getStatus
import com.programotuojes.survival_games.extensions.setStatus
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Ready : BaseCommand {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Command must be executed as a player")
            return false
        }

        val arenaName = sender.getArenaName()
        if (arenaName == null) {
            sender.sendMessage("You are not in a match")
            return false
        }

        val arena = PluginState.arenas.getOrElse(arenaName) {
            sender.sendMessage("You are not in an arena")
            return false
        }

        sender.setStatus(PlayerStatus.READY)

        val readyCount = arena.players.values.filter { it.getStatus() == PlayerStatus.READY }.size
        val playerSize = arena.players.size

        arena.players.forEach { it.value.sendMessage("$readyCount out of $playerSize players ready") }

        if (playerSize == readyCount) {
            arena.startMatch()
        }

        return true
    }

    override fun getSuggestions(args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }
}
