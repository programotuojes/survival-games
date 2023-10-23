package com.programotuojes.survival_games.tasks

import com.programotuojes.survival_games.data.PluginState
import com.programotuojes.survival_games.extensions.secToTicks
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import kotlin.math.absoluteValue

class LoopTask(
    private val iterator: IntIterator,
    private val action: (i: Int) -> Unit,
    private val actionAfter: (() -> Unit)?,
) : BukkitRunnable() {

    override fun run() {
        if (!iterator.hasNext()) {
            cancel()
            actionAfter?.invoke()
            return
        }

        action(iterator.nextInt())
    }

    companion object {
        fun runAfter(
            delay: Int,
            range: IntProgression,
            action: (i: Int) -> Unit,
            actionAfter: (() -> Unit)? = null
        ): BukkitTask {
            return LoopTask(range.iterator(), action, actionAfter).runTaskTimer(
                PluginState.plugin,
                delay.secToTicks(),
                range.step.absoluteValue.secToTicks()
            )
        }
    }
}
