package com.programotuojes.survival_games.commands

import org.bukkit.command.CommandSender

interface BaseCommand {
    fun execute(sender: CommandSender, args: Array<out String>): Boolean
    fun getSuggestions(args: Array<out String>): MutableList<String>
}
