package org.banana_inc

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent
import org.banana_inc.EventManager.HandlerRemover
import org.banana_inc.EventManager.addHandler
import org.banana_inc.EventManager.removeHandler
import org.banana_inc.extensions.resolve
import org.banana_inc.extensions.sendError
import org.banana_inc.util.reflection.ClassGraph
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object EventManager: Listener {

    @PublishedApi
    internal var events: ConcurrentHashMap<KClass<out Event>, ArrayDeque<(Event) -> Unit>> = ConcurrentHashMap()

    private val canceledEvents: Set<KClass<out Event>> = setOf(
        PlayerLoginEvent::class,
        PlayerAdvancementDoneEvent::class //temp until further system made
    ).filter { it.java.isAssignableFrom(Cancellable::class.java) }  // Filter only Cancellable events
        .toSet()

    init {
        val pm = plugin.server.pluginManager
        for(i in ClassGraph.allBukkitEventClasses){
            pm.registerEvent(i,this, EventPriority.HIGH, { _, event ->
                if(!i.isInstance(event)) return@registerEvent
                if (event is Cancellable && canceledEvents.contains(event::class)) event.isCancelled = true
                handleEvent(event)
            }, plugin)
        }
    }

    private fun handleEvent(event: Event) {
        if (event is Cancellable && event::class in canceledEvents) {
            event.isCancelled = true
        }
        events[event::class]?.toList()?.forEach { handler ->
            handler(event)
        }
    }



    @PublishedApi
    internal fun addHandler(eventClass: KClass<out Event>, handler: EventHandler): HandlerRemover {
        events.compute(eventClass) { _, handlers ->
            (handlers ?: ArrayDeque()).apply { addFirst(handler) }
        }
        return HandlerRemover { removeHandler(eventClass, handler) }
    }

    @PublishedApi
    internal fun removeHandler(eventClass: KClass<out Event>, handler: EventHandler) {
        events[eventClass]?.remove(handler)
    }

    fun interface HandlerRemover {
        fun remove()
    }

}

inline fun <reified T : Event> on(noinline action: T.() -> Unit) = addHandler(T::class) { action(it as T) }

inline fun <reified T : Event> once(
    crossinline filter: (T) -> Boolean,
    crossinline action: T.() -> Unit
): HandlerRemover {
    lateinit var handler: EventHandler
    handler = {
        (it as T).takeIf(filter)?.let { event ->
            action(event)
            removeHandler(T::class, handler)
        }
    }
    return addHandler(T::class,handler)
}

inline fun <reified T : Event> once(crossinline action: (T) -> Unit) = once({ true }, action)

inline fun <reified T : PlayerEvent> onPlayer(
    player: Player,
    noinline action: T.() -> Unit
): HandlerRemover {
    val handler = on<T> { if (this.player == player) action(this) }

    if (T::class != PlayerQuitEvent::class) {
        once<PlayerQuitEvent>({  it.player == player }) { handler.remove() }
    }

    return handler
}

inline fun <reified T : PlayerEvent> oncePlayer(
    player: Player,
    crossinline filter: T.() -> Boolean,
    crossinline action: T.() -> Unit
): HandlerRemover {
    lateinit var handler: HandlerRemover
    handler = onPlayer<T>(player) {
        if (filter(this)) {
            action(this)
            handler.remove()
        }
    }
    return handler
}

inline fun <reified T : PlayerEvent> oncePlayer(player: Player, crossinline action: T.() -> Unit): HandlerRemover{
    return oncePlayer<T>(player, {true}, action)
}

inline fun <reified T : Any> Player.onChat(noinline action: (T) -> Unit) = onChat(T::class, action)

fun <T : Any> Player.onChat(type: KClass<T>, action: (T) -> Unit) {
    oncePlayer<AsyncChatEvent>(this) {
        isCancelled = true
        val content = (message() as TextComponent).content()
        runCatching { action(content.resolve(type)) }
            .onFailure {
                sendError(player, "Invalid input! Expected: ${type.simpleName}")
                onChat(type, action)
        }
    }
}

private typealias EventHandler = (Event) -> Unit

