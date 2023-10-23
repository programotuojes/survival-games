package com.programotuojes.survival_games.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class BaseHandler : CommandExecutor, TabCompleter {

    private val subCommands = hashMapOf(
        Pair("create", Create()),
        Pair("cancel", Cancel()),
        Pair("finalize", Finalize()),
        Pair("addSpawn", AddSpawn()),
        Pair("join", Join()),
        Pair("ready", Ready()),
        Pair("reset", Reset()),
        Pair("quit", Quit()),
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (args == null || args.isEmpty()) {
            // TODO show help
            sender.sendMessage("Supported commands: ${subCommands.keys.joinToString(", ")}")
            return false
        }

        val cmd = subCommands.getOrElse(args[0]) {
            sender.sendMessage("Command not found")
            sender.sendMessage("Supported commands: ${subCommands.keys.joinToString(", ")}")
            return false
        }

        return cmd.execute(sender, args)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String> {
        if (args == null || args.isEmpty()) {
            return subCommands.keys.toMutableList()
        }

        if (args.size == 1) {
            return subCommands.keys.filter { it.startsWith(args.first()) }.toMutableList()
        }

        val cmd = subCommands.getOrElse(args.first()) {
            return mutableListOf()
        }

        return cmd.getSuggestions(args)
    }
}
