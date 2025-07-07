package org.banana_inc.util

import net.kyori.adventure.extra.kotlin.join
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.HoverEvent
import org.banana_inc.extensions.component
import org.banana_inc.extensions.minimessage
import org.banana_inc.extensions.readableName
import org.banana_inc.logger
import org.banana_inc.util.initialization.InitOnStartup
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

val <T: Any> T.readable: Component
    get() = ComponentResolver.resolve(this)

@InitOnStartup
object ComponentResolver{

    val resolvers: MutableMap<KClass<*>, (Any) -> Component> = mutableMapOf()

    init {
        addResolver<String>{ it.component }
        addResolver<Component>{ (it.minimessage + "<reset>").component }
        addResolver<Enum<*>>{ it -> it.readableName().component }
        addResolver<KClass<*>>{ it -> it.simpleName!!.component  }
    }

    fun resolve(input: Any): Component {
        val clazz = input::class
        logger.info("input $input")
        if(input is KClass<*> && input.isSubclassOf(Enum::class)) { // special case for enums
            return input.simpleName!!.component.hoverEvent(HoverEvent.showText(input.java.enumConstants.map {
                    it.readable
                }.join(JoinConfiguration.newlines()))
            )
        }
        if(clazz == Component::class || clazz.isSubclassOf(Component::class)) {
            return input as Component
        }
        var resolver = resolvers[clazz]
        if(resolver == null){
            resolver = resolvers.filter{ entry -> clazz.isSubclassOf(entry.key) }.entries.firstOrNull()?.value
        }

        return (resolver ?: return input.toString().component).invoke(input)
    }


    private inline fun <reified T : Any> addResolver(noinline resolver: (T) -> Component) {
        resolvers[T::class] = { input ->
            try {
                resolver(input as T)
            }catch (e: Exception){
                throw ResolutionException("Failed to resolve '$input' as ${T::class.simpleName}: ${e.message}")
            }
        }
    }


}
