package com.zorbeytorunoglu.kLib.task

import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.PI

class RepeatRunnable(private val repeat: Repeat, private val function: (RepeatRunnable) -> Unit): BukkitRunnable() {

    private var progress: Double = 0.0

    override fun run() {
        if(progress>=repeat.duration && repeat.duration != -1L) cancel()
        function(this)
        progress += repeat.period
    }

    fun getProgressSeconds(): Double = progress / repeat.unitsPerSec

    fun getPercentage(): Double = if(repeat.duration != -1L) progress / repeat.duration else 0.0

    fun getRadians(): Double = getProgressSeconds() * 2 * PI

}