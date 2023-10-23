package com.programotuojes.survival_games.commands

import com.programotuojes.survival_games.data.PluginState
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AddSpawn : BaseCommand {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Command must be executed as a player")
            return false
        }

        val arenaName = args.getOrElse(1) {
            sender.sendMessage("Arena name must be provided")
            return false
        }

        val arena = PluginState.pendingArenas.getOrElse(arenaName) {
            sender.sendMessage("Arena $arenaName not found")
            return false
        }

        val targetBlock = sender.getTargetBlock(40)
        if (targetBlock == null) {
            sender.sendMessage("No block found within 40 blocks")
            return false
        }

        arena.spawnPoints.add(targetBlock.location.add(0.5, 1.0, 0.5))
        sender.sendMessage("Spawn point ${arena.spawnPoints.size} added (${targetBlock.blockData.asString.split(':')[1]})")
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
