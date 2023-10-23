package com.programotuojes.survival_games.commands

import com.programotuojes.survival_games.data.Arena
import com.programotuojes.survival_games.data.PluginState
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Create : BaseCommand {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Command must be executed as a player")
            return false
        }

        if (args.size != 2) {
            // TODO usage
            sender.sendMessage("Arena name must be specified")
            return false
        }

        val arenaName = args[1]

        val existingArena = PluginState.arenas[arenaName]
        if (existingArena != null) {
            sender.sendMessage("Arena with name \"$arenaName\" already exists. Use /sg join $arenaName")
            return false
        }

        val pendingArena = PluginState.pendingArenas[arenaName]
        if (pendingArena != null) {
            sender.sendMessage("Arena \"$arenaName\" hasn't been finalized yet")

            val spawnCount = pendingArena.spawnPoints.size
            val otherMessage = if (spawnCount < 2)
                "Arenas must have at least 2 spawn points (current count: $spawnCount). Add them with /sg addSpawn $arenaName" else
                "It has $spawnCount spawn points. If that's enough, call /sg finalize $arenaName"

            sender.sendMessage(otherMessage)
            return false
        }

        val targetBlock = sender.getTargetBlock(15)
        if (targetBlock == null) {
            sender.sendMessage("No block found within 15 blocks")
            return false
        }

        PluginState.pendingArenas[arenaName] = Arena(arenaName, targetBlock.location.add(0.5, 1.0, 0.5))

        sender.sendMessage("Arena created (${targetBlock.blockData.asString.split(':')[1]} is the center)")
        sender.sendMessage("Now add spawn points using /sg addSpawn $arenaName")
        return true
    }

    override fun getSuggestions(args: Array<out String>): MutableList<String> {
        if (args.size == 2) {
            return mutableListOf("<name>")
        }

        return mutableListOf()
    }
}
