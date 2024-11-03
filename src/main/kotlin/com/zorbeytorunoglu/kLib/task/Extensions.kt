package com.zorbeytorunoglu.kLib.task

import kotlinx.coroutines.Runnable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

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



fun JavaPlugin.delaySync(function: () -> Unit, seconds: Int): BukkitTask {

    return this.server.scheduler.runTaskLater(this, Runnable {
        function()
    },seconds*20L)

}

fun JavaPlugin.delayync(function: () -> Unit, millis: Long): BukkitTask {

    return this.server.scheduler.runTaskLater(this, Runnable {
        function()
    }, millis)

}


fun JavaPlugin.delayAsync(function: () -> Unit, seconds: Int): BukkitTask {

    return this.server.scheduler.runTaskLaterAsynchronously(this, Runnable {
        function()
    },seconds*20L)

}

fun JavaPlugin.delayAsync(function: () -> Unit, millis: Long): BukkitTask {

    return this.server.scheduler.runTaskTimer(this, Runnable {
        function()
    }, millis,2)

}




