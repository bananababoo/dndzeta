package com.zorbeytorunoglu.kLib.task

import org.banana_inc.plugin
import org.bukkit.scheduler.BukkitTask

abstract class Repeat(
    var period: Long,
    var duration: Long,
    var delay: Long,
    private val function: (RepeatRunnable) -> Unit
) {

    val task: BukkitTask = run {
        runRunnable(RepeatRunnable(this,function))
    }

    abstract fun runRunnable(runnable: RepeatRunnable): BukkitTask
    abstract val unitsPerSec: Long

    class Sync(
        periodTicks: Long = 1,
        durationTicks: Long = -1,
        delayTicks: Long = 0,
        function: (RepeatRunnable) -> Unit,
        override val unitsPerSec: Long = 20
    ): Repeat(periodTicks,durationTicks,delayTicks,function) {
        constructor(periodSecs: Int = 1, durationSecs: Int = -1, delaySecs: Int = 0, function: (RepeatRunnable) -> Unit):
                this(periodSecs * 20L, durationSecs * 20L, delaySecs * 20L, function)
        constructor(periodTicks: Long = 1, durationSecs: Int = -1, delaySecs: Int = 0, function: (RepeatRunnable) -> Unit):
                this(periodTicks, durationSecs * 20L, delaySecs * 20L, function)
        override fun runRunnable(runnable: RepeatRunnable): BukkitTask = runnable.runTaskTimer(plugin,delay,period)

    }

    class Async(
        periodMillis: Long = 50,
        durationMillis: Long = -1,
        delayMillis: Long = 0,
        function: (RepeatRunnable) -> Unit,
        override val unitsPerSec: Long = 1000,
    ): Repeat(periodMillis,durationMillis,delayMillis,function) {
        constructor(periodSecs: Int = 1, durationSecs: Int = -1, delaySecs: Int = 0, function: (RepeatRunnable) -> Unit):
                this(periodSecs * 1000L, durationSecs * 1000L, delaySecs * 1000L, function)
        constructor(periodMillis: Long = 50, durationSecs: Int = -1, delaySecs: Int = 0, function: (RepeatRunnable) -> Unit):
                this(periodMillis, durationSecs * 1000L, delaySecs * 1000L, function)
        override fun runRunnable(runnable: RepeatRunnable): BukkitTask = runnable.runTaskTimerAsynchronously(plugin,delay,period)

    }

}