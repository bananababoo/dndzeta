package com.zorbeytorunoglu.kLib.task

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.banana_inc.plugin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class Repeat(
    var period: Long,
    var duration: Long,
    var delay: Long,
    private val function: (RepeatRunnable) -> Unit
) {

    val task: CancelableTask = run {
        runRunnable(RepeatRunnable(this,function))
    }

    abstract fun runRunnable(runnable: RepeatRunnable): CancelableTask
    abstract val unitsPerSec: Long

    class Sync(
        periodSecs: Int = 0, periodTicks: Long = 1,
        durationSecs: Int = 0, durationTicks: Long = -1,
        delaySecs: Int = 0, delayTicks: Int = 0,
        override val unitsPerSec: Long = 20,
        function: (RepeatRunnable) -> Unit
    ): Repeat(periodSecs * 20L + periodTicks, durationSecs * 20L + durationTicks, delaySecs * 20L + delayTicks,function) {
        override fun runRunnable(runnable: RepeatRunnable): CancelableTask = SyncTask(runnable.runTaskTimer(plugin,delay,period))
    }

    class Async(
        period: Duration = (1.0/20.0).seconds,
        duration: Duration = Duration.INFINITE,
        delay: Duration = Duration.ZERO,
        override val unitsPerSec: Long = 1000,
        function: (RepeatRunnable) -> Unit
    ): Repeat(period.inWholeMilliseconds, duration.inWholeMilliseconds, delay.inWholeMilliseconds,function) {

        override fun runRunnable(runnable: RepeatRunnable): CancelableTask = AsyncTask(
            Scopes.defaultScope.launch {
                delay(delay)
                repeat((duration / period).toInt()) {
                    for (i in 1..duration) {
                        runnable.run()
                        delay(period)
                    }
                }

            }
        )

    }

}