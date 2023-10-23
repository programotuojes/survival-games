package com.programotuojes.survival_games.commands

import com.programotuojes.survival_games.data.ArenaStatus
import com.programotuojes.survival_games.data.PlayerData
import com.programotuojes.survival_games.data.PlayerStatus
import com.programotuojes.survival_games.data.PluginState
import com.programotuojes.survival_games.extensions.reset
import com.programotuojes.survival_games.extensions.setArenaName
import com.programotuojes.survival_games.extensions.setStatus
import io.papermc.paper.entity.LookAnchor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Join : BaseCommand {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Command must be executed as a player")
            return false
        }

        val arenaName = args.getOrElse(1) {
            sender.sendMessage("Arena name must be provided")
            return false
        }

        val arena = PluginState.arenas.getOrElse(arenaName) {
            PluginState.pendingArenas.getOrElse(arenaName) {
                sender.sendMessage("Arena $arenaName hasn't been finalized")
                sender.sendMessage("Run \"/sg finalize $arenaName\" to finish setting it up")
                return false
            }

            sender.sendMessage("Arena $arenaName not found")
            return false
        }

        val playerCount = arena.players.size
        if (playerCount >= arena.spawnPoints.size) {
            sender.sendMessage("$arenaName is full")
            return false
        }

        if (arena.status != ArenaStatus.READY) {
            sender.sendMessage("$arenaName is not ready for new players")
            return false
        }

        PluginState.previousPlayerData[sender.uniqueId] = PlayerData(sender)

        val teleported = sender.teleport(arena.spawnPoints[playerCount])
        if (!teleported) {
            sender.sendMessage("Failed to teleport")
            PluginState.previousPlayerData.remove(sender.uniqueId)
            return false
        }

        arena.players[sender.uniqueId] = sender
        sender.reset()

        @Suppress("UnstableApiUsage")
        sender.lookAt(arena.center, LookAnchor.EYES)
        sender.setStatus(PlayerStatus.JOINED)
        sender.setArenaName(arenaName)

        // Set the world border when the first player joins
        if (playerCount == 0) {
            val border = sender.location.world.worldBorder
            border.center = arena.center
            border.size = (arena.radiusStart * 2).toDouble()
        }

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
