package com.programotuojes.survival_games.commands

import com.programotuojes.survival_games.data.PluginState
import com.programotuojes.survival_games.extensions.getArenaName
import com.programotuojes.survival_games.extensions.restore
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Quit : BaseCommand {

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
            sender.sendMessage("Arena \"$arenaName\" not found")
            return false
        }

        val isInArena = arena.players.containsKey(sender.uniqueId)
        if (!isInArena) {
            sender.sendMessage("You are not in a match")
            return false
        }

        arena.players.remove(sender.uniqueId)

        if (arena.players.isEmpty()) {
            arena.center.world.worldBorder.reset()
        }

        val previousData = PluginState.previousPlayerData.remove(sender.uniqueId)
        if (previousData == null) {
            sender.sendMessage("Your previous data was not saved")
            return false
        }

        sender.restore(previousData)
        arena.bossBar.removePlayer(sender)

        return true
    }

    override fun getSuggestions(args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }
}
