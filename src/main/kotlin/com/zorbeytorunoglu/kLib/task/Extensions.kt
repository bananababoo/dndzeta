package com.zorbeytorunoglu.kLib.task

import kotlinx.coroutines.Runnable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.time.Duration

/**
 * Suspends the function in ASYNC.
 * @param function Unit
 */
suspend fun JavaPlugin.suspendFunctionAsync(function: () -> Unit) {
    Scopes.supervisorScope.launch(MCDispatcher(this, async = true)) {
        function()
    }.join()
}

suspend fun JavaPlugin.suspendFunctionSync(function: () -> Unit) {
    Scopes.supervisorScope.launch(MCDispatcher(this, async = false)) {
        function()
    }.join()
}

suspend fun JavaPlugin.suspendFunction(function: () -> Unit, async: Boolean) {
    Scopes.supervisorScope.launch(MCDispatcher(this, async = async)) {
        function()
    }.join()
}

suspend fun <T> JavaPlugin.suspendFunctionSyncWithResult(function: () -> T): T {

    return Scopes.supervisorScope.async(MCDispatcher(this,async = false)) {

        function()

    }.await()

}

suspend fun <T> JavaPlugin.suspendFunctionAsyncWithResult(function: () -> T): T {

    return Scopes.supervisorScope.async(MCDispatcher(this,async = true)) {

        function()

    }.await()

}

suspend fun <T> JavaPlugin.suspendFunctionWithResult(async: Boolean, function: () -> T): T {
    return Scopes.supervisorScope.async(MCDispatcher(this,async)) {

        function()

    }.await()

}


fun JavaPlugin.sync(function: () -> Unit): BukkitTask {
    return this.server.scheduler.runTaskLater(this, Runnable {
        function()
    },0L)
}


infix fun JavaPlugin.nextTick(function: () -> Unit): BukkitTask {
    return this.server.scheduler.runTask(this, function)
}

fun JavaPlugin.delaySync(time: Duration, function: () -> Unit): BukkitTask {
    return this.server.scheduler.runTaskLater(this, function, time.seconds * 20)
}

fun JavaPlugin.delaySync(ticks: Long, function: () -> Unit): BukkitTask {
    return this.server.scheduler.runTaskLater(this, function, ticks)
}

fun JavaPlugin.delayAsync(time: Duration, function: () -> Unit): BukkitTask {
    return this.server.scheduler.runTaskLaterAsynchronously(this,  function,time.seconds*20L)
}

fun JavaPlugin.delayAsync( millis: Long, function: () -> Unit): BukkitTask {
    return this.server.scheduler.runTaskLaterAsynchronously(this, function, millis)
}

fun JavaPlugin.timer(seconds: Int, delay: Long = 0,  function: () -> Unit): BukkitTask {
    return this.server.scheduler.runTaskTimer(this, function, delay * 20L, seconds * 20L)
}


