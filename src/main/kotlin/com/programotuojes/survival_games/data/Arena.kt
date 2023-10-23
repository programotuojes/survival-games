package com.programotuojes.survival_games.data

import com.programotuojes.survival_games.extensions.getStatus
import com.programotuojes.survival_games.extensions.restore
import com.programotuojes.survival_games.extensions.setStatus
import com.programotuojes.survival_games.tasks.LoopTask
import com.programotuojes.survival_games.tasks.SingleTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*

class Arena(val name: String, val center: Location) {

    private var _radiusStart: Int? = null
    val radiusStart get() = _radiusStart ?: PluginState.config.getInt("$ARENA_DEFAULTS.$RADIUS_START")

    private var _radiusEnd: Int? = null
    private val radiusEnd get() = _radiusEnd ?: PluginState.config.getInt("$ARENA_DEFAULTS.$RADIUS_END")

    private var _matchDuration: Int? = null
    private val matchDuration get() = _matchDuration ?: PluginState.config.getInt("$ARENA_DEFAULTS.$MATCH_DURATION")

    private var _chestRefill: Int? = null
    val chestRefill get() = _chestRefill ?: PluginState.config.getInt("$ARENA_DEFAULTS.$CHEST_REFILL", 0)

    private var _teleportToSpawn: Int? = null
    private val teleportToSpawn get() = _teleportToSpawn ?: PluginState.config.getInt("$ARENA_DEFAULTS.$TELEPORT_TO_SPAWN", 0)

    private var _borderShrinkStart: Int? = null
    private val borderShrinkStart get() = _borderShrinkStart ?: PluginState.config.getInt("$ARENA_DEFAULTS.$BORDER_SHRINK_START", 0)

    private var _borderShrinkEnd: Int? = null
    private val borderShrinkEnd get() = _borderShrinkEnd ?: PluginState.config.getInt("$ARENA_DEFAULTS.$BORDER_SHRINK_END", 0)

    private var _gracePeriod: Int? = null
    private val gracePeriod get() = _gracePeriod ?: PluginState.config.getInt("$ARENA_DEFAULTS.$GRACE_PERIOD", 0)

    val spawnPoints = mutableListOf<Location>()
    val players = mutableMapOf<UUID, Player>()
    val bossBar = Bukkit.createBossBar("Match duration", BarColor.WHITE, BarStyle.SOLID)
    var status = ArenaStatus.READY
    var allowDamage = false

    fun addToConfig() {
        val arenas = PluginState.plugin.config.getConfigurationSection(ARENAS) ?: PluginState.plugin.config.createSection(ARENAS)
        val section = arenas.createSection(name)

        section[WORLD] = center.world.name
        section[CENTER] = String.format("%.1f %.1f %.1f", center.x, center.y, center.z)
        section[SPAWN_POINTS] = spawnPoints.map { String.format("%.1f %.1f %.1f", it.x, it.y, it.z) }

        section[RADIUS_START] = _radiusStart
        section[RADIUS_END] = _radiusEnd
        section[MATCH_DURATION] = _matchDuration
        section[CHEST_REFILL] = _chestRefill
        section[TELEPORT_TO_SPAWN] = _teleportToSpawn
        section[BORDER_SHRINK_START] = _borderShrinkStart
        section[BORDER_SHRINK_END] = _borderShrinkEnd
        section[GRACE_PERIOD] = _gracePeriod

        PluginState.plugin.saveConfig()
    }

    fun setFromConfig(section: ConfigurationSection) {
        _matchDuration = section.get(MATCH_DURATION) as Int?
        _chestRefill = section.get(CHEST_REFILL) as Int?
        _teleportToSpawn = section.get(TELEPORT_TO_SPAWN) as Int?
        _borderShrinkStart = section.get(BORDER_SHRINK_START) as Int?
        _borderShrinkEnd = section.get(BORDER_SHRINK_END) as Int?
        _radiusStart = section.get(RADIUS_START) as Int?
        _radiusEnd = section.get(RADIUS_END) as Int?
    }

    fun reset() {
        status = ArenaStatus.READY
        allowDamage = false
        players.values.forEach {
            val previousData = PluginState.previousPlayerData.remove(it.uniqueId)
            if (previousData == null) {
                it.sendMessage("Your previous data was not saved")
                return@forEach
            }

            it.restore(previousData)
        }
        players.clear()
        bossBar.removeAll()
        center.world.worldBorder.reset()
    }

    fun startMatch() {
        if (status != ArenaStatus.READY) return

        status = ArenaStatus.STARTING
        players.values.forEach { it.sendMessage("All players are ready. Match will begin in 10 seconds") }

        LoopTask.runAfter(5, 5 downTo 1, { i ->
            players.values.forEach { it.sendMessage("$i...") }
        }, {
            players.values.forEach {
                status = ArenaStatus.RUNNING
                it.setStatus(PlayerStatus.PLAYING)
                it.sendMessage("Start!")
                it.sendMessage("You have $gracePeriod seconds before the grace period ends")
                bossBar.addPlayer(it)
            }

            matchTimer()
            teleportToSpawn()
            shrinkBorder()
            gracePeriod()
        })
    }

    fun stopIfLastPlayer() {
        val remainingPlayers = players.values.filter { it.getStatus() == PlayerStatus.PLAYING }
        if (remainingPlayers.size != 1) return

        players.values.forEach { it.sendMessage("Game ended! ${remainingPlayers.first().name} won") }

        SingleTask.runAfter(2) { reset() }
    }

    private fun matchTimer() {
        val matchDurationDouble = matchDuration.toDouble()
        val totalPlayers = players.values.size

        LoopTask.runAfter(0, matchDuration downTo 0, { i ->
            val alivePlayers = players.values.count { it.getStatus() == PlayerStatus.PLAYING }
            bossBar.progress = i / matchDurationDouble
            bossBar.setTitle("Match duration: $i sec. $alivePlayers/$totalPlayers")
        }, {
            players.values.forEach { it.sendMessage("Match ended!") }
            reset()
        })
    }

    private fun teleportToSpawn() {
        if (teleportToSpawn == 0) return

        SingleTask.runAfter(matchDuration - teleportToSpawn) {
            players.values.forEachIndexed { index, player -> player.teleport(spawnPoints[index]) }
        }
    }

    private fun shrinkBorder() {
        if (borderShrinkStart == 0 || borderShrinkEnd == 0) return

        SingleTask.runAfter(matchDuration - borderShrinkStart) {
            center.world.worldBorder.setSize(
                (radiusEnd * 2).toDouble(),
                (borderShrinkStart - borderShrinkEnd).toLong()
            )
        }
    }

    private fun gracePeriod() {
        if (gracePeriod == 0) return

        SingleTask.runAfter(gracePeriod) {
            allowDamage = true
            players.values.forEach { it.sendMessage("Grace period ended!") }
        }
    }

    companion object {
        const val ARENAS = "arenas"
        const val ARENA_DEFAULTS = "arenaDefaults"

        const val WORLD = "world"
        const val CENTER = "center"
        const val SPAWN_POINTS = "spawnPoints"
        const val RADIUS_START = "radiusStart"
        const val RADIUS_END = "radiusEnd"
        const val MATCH_DURATION = "times.matchDuration"
        const val CHEST_REFILL = "times.chestRefill"
        const val TELEPORT_TO_SPAWN = "times.teleportToSpawn"
        const val BORDER_SHRINK_START = "times.borderShrinkStart"
        const val BORDER_SHRINK_END = "times.borderShrinkEnd"
        const val GRACE_PERIOD = "times.gracePeriod"

        fun loadArenas() {
            val arenas = PluginState.config.getConfigurationSection(ARENAS) ?: return

            for (arenaName in arenas.getKeys(false)) {
                val section = PluginState.config.getConfigurationSection("$ARENAS.$arenaName")!!

                val worldString = section.getString(WORLD)!!
                val world = PluginState.plugin.server.getWorld(worldString)

                val centerCoords = section.getString(CENTER)!!.split(' ').map { it.toDouble() }
                val center = Location(world, centerCoords[0], centerCoords[1], centerCoords[2])

                val arena = Arena(arenaName, center)
                arena.setFromConfig(section)

                for (spawnString in section.getStringList(SPAWN_POINTS)) {
                    val coords = spawnString.split(' ').map { it.toDouble() }
                    val spawnPoint = Location(world, coords[0], coords[1], coords[2])
                    arena.spawnPoints.add(spawnPoint)
                }

                PluginState.arenas[arenaName] = arena
            }
        }
    }
}
