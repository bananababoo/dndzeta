package org.banana_inc

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent
import org.banana_inc.extensions.resolve
import org.banana_inc.extensions.sendError
import org.banana_inc.util.reflection.ClassGraph
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerLoginEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass

object EventManager: Listener {

    @PublishedApi
    internal var events: ConcurrentHashMap<KClass<out Event>, CopyOnWriteArraySet<(Event) -> Unit>> = ConcurrentHashMap()

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
                if (event is Cancellable && canceledEvents.contains(event::class))
                    event.isCancelled = true
                handleEvent(event)
            }, plugin)
        }
    }


    /**
     * returns a reference to itself
     */
    inline fun <reified T : Event> addListenerWithSelfReference(noinline action: (T, (Event) -> Unit) -> Unit) {
        lateinit var reference: (Event) -> Unit
        reference = { event ->
            action(event as T, reference)
        }
        events.compute(T::class) { _, existingActions ->
            (existingActions ?: CopyOnWriteArraySet()).apply { add(reference) }
        }
    }

    inline fun <reified T : Event> addListener(noinline action: T.() -> Unit) {
        events.compute(T::class) { _, existingActions ->
            (existingActions ?: CopyOnWriteArraySet<(Event) -> Unit>()).apply { add{ action(it as T) } }
        }
    }


    inline fun <reified T : Event> callback(crossinline action: (T) -> Unit) {
        callback<T>({ true },action)
    }

    inline fun <reified T : Event> callback(crossinline filter: (T) -> Boolean, crossinline action: (T) -> Unit) {
        val callback: (T,(Event) -> Unit) -> Unit = { it, reference ->
            if(filter(it)) {
                action(it)
                logger.info(events[T::class]!!.remove(reference).toString())
            }
        }
        addListenerWithSelfReference<T>(callback)
    }

    inline fun <reified T : Any> Player.chatCallback(noinline action: (T) -> Unit ){
        chatCallback(T::class, action)
    }

    fun <T : Any> Player.chatCallback(type: KClass<T>, action: (T) -> Unit ){
        callback<AsyncChatEvent> ({ it.player == this }){
            it.isCancelled = true
            val content = (it.message() as TextComponent).content()
            try{
                action(content.resolve(type))
            }catch (e: IllegalStateException){
                sendError(this, "Invalid type! Try again and make sure its a ${type.simpleName}")
                chatCallback(type,action)
            }
        }
    }

    private fun handleEvent(event: Event) {
        val handlers = events[event::class]
        for (handler in handlers?: return) {
            handler(event)
        }
    }

}
