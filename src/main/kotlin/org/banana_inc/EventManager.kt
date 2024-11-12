package org.banana_inc

import org.banana_inc.util.reflection.ClassGraph
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object EventManager: Listener {

    @PublishedApi
    internal var map: ConcurrentHashMap<KClass<out Event>, MutableSet<(Event) -> Unit>> = ConcurrentHashMap()

    init {
        val pm = plugin.server.pluginManager
        for(i in ClassGraph.getAllBukkitEventClasses()){
            pm.registerEvent(i,this, EventPriority.HIGH, { _, event ->
                handleEvent(event)
            }, plugin)
        }
    }

    inline fun <reified T : Event> addListener(noinline action: (T) -> Unit) {
        map.compute(T::class) { _, existingActions ->
            existingActions
                ?.apply { add { action(it as T) } }
                ?: mutableSetOf<(Event) -> Unit>().also { it -> it.add { action(it as T) } }
        }
    }

    private fun handleEvent(event: Event) {
        val handlers = map[event::class]
        for (handler in handlers?: return) {
            handler(event)
        }
    }

}
