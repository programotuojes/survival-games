package com.programotuojes.survival_games.tasks

import com.programotuojes.survival_games.data.PluginState
import com.programotuojes.survival_games.extensions.secToTicks
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class SingleTask(private val action: () -> Unit) : BukkitRunnable() {

    override fun run() = action()

    companion object {
        fun runAfter(delay: Int, action: () -> Unit): BukkitTask {
            return SingleTask(action).runTaskLater(PluginState.plugin, delay.secToTicks())
        }
    }
}
