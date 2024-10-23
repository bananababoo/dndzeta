package org.banana_inc.extensions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

private object Dispatch {
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
}
/**
 * Register multiple events.
 * @param listeners vararg of listener classes.
 */
fun JavaPlugin.registerEvents(vararg listeners: Listener) {
    listeners.forEach { server.pluginManager.registerEvents(it, this) }
}

/**
 * Logs an info log.
 * @param log string to be logged.
 */
fun JavaPlugin.logInfo(log: String) = logger.info(log)

/**
 * Logs a severe log.
 * @param log string to be logged.
 */
fun JavaPlugin.logSevere(log: String) = logger.severe(log)

/**
 * Logs a warning log.
 * @param log string to be logged.
 */
fun JavaPlugin.logWarning(log: String) = logger.warning(log)

