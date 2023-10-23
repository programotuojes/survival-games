package com.programotuojes.survival_games.listeners

import com.programotuojes.survival_games.data.PlayerStatus
import com.programotuojes.survival_games.extensions.getArena
import com.programotuojes.survival_games.extensions.getLastRefillTime
import com.programotuojes.survival_games.extensions.getStatus
import com.programotuojes.survival_games.extensions.setLastRefillTime
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class ChestListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onChestOpen(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK || !isChest(event.clickedBlock)) return

        val status = event.player.getStatus()
        if (status != PlayerStatus.PLAYING) return

        val arena = event.player.getArena() ?: return
        val chest = event.clickedBlock!!
        val contents = (chest.state as InventoryHolder).inventory

        val timeDifference = System.currentTimeMillis() - (chest.getLastRefillTime() ?: 0)

        if (arena.chestRefill == 0 || timeDifference < arena.chestRefill * 1000) return

        chest.setLastRefillTime()
        contents.clear()
        contents.setItem(0, ItemStack(Material.NETHERITE_SWORD, 1))
        contents.setItem(1, ItemStack(Material.TRAPPED_CHEST, 1))
        contents.setItem(2, ItemStack(Material.ACACIA_BOAT, 1))
        contents.setItem(3, ItemStack(Material.GRAY_CANDLE, 42))
    }

    private fun isChest(clickedBlock: Block?): Boolean {
        return clickedBlock?.type == Material.CHEST || clickedBlock?.type == Material.TRAPPED_CHEST
    }
}
