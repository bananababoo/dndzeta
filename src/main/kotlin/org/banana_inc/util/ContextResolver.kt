package org.banana_inc.util

import org.banana_inc.extensions.component
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import kotlin.reflect.KClass
import kotlin.reflect.cast

class ResolutionException(message: String) : Exception(message)

@InitOnStartup
object ContextResolver{

    val resolvers: MutableMap<KClass<*>, (String, Any?) -> Any> = mutableMapOf()

    init {
        addResolver { it }
        addResolver { it.toFloat() }
        addResolver { it.toDouble() }
        addResolver { it.toInt() }
        addResolver { it.toLong() }
        addResolver { it.toShort() }
        addResolver { it.split(",").map { part -> resolve<Float>(part) } }
        addResolver { it.split(",").map { part -> resolve<Double>(part) } }
        addResolver { it.split(",").map { part -> resolve<Int>(part) } }
        addResolver { it.split(",").map { part -> resolve<Long>(part) } }
        addResolver { it.split(",").map { part -> part } }
        addResolver { it.toBooleanStrictOrNull() ?: throw ResolutionException("Boolean must be 'true' or 'false', but was '$it'") }
        addResolver { it.component }
        addResolver { Bukkit.getOnlinePlayers().find { players -> it.equals(players.name,true) }!! }
        addResolver { Bukkit.getWorld(it)!! }
        addResolver {
            val parts = it.split(",")
            if (parts.size != 4) error("Location format should be 'world,x,y,z'")
            Location(resolve<World>(parts[0]), resolve<Double>(parts[1]), resolve<Double>(parts[2]), resolve<Double>(parts[3]))
        }
    }

    inline fun <reified T : Any, U : Any> resolve(input: String, context: U? = null ): T {
        return resolvers[T::class]!!.invoke(input,context) as T
    }

    fun <T : Any> resolve(input: String, clazz: KClass<T>): T {
        if(clazz.java.isEnum){
            return clazz.java.enumConstants.firstOrNull {
                (it as Enum<*>).name.equals(input, true)
            }?: error("Can't find enum $input in ${clazz.simpleName} when resolving")
        }
        if(clazz == String::class){
            return clazz.cast(input)
        }
        return clazz.cast((resolvers[clazz]?:error("No Context Resolver for Type ${clazz.simpleName}")).invoke(input,null))
    }

    inline fun <reified T : Any> resolve(input: String): T {
        return resolve(input, T::class)
    }


    private inline fun <reified T : Any> addResolver(noinline resolver: (String) -> T) {
        resolvers[T::class] = { input, extra ->
            if(extra != null) error("Passing context into resolver not created with any! $extra")
            try {
                resolver(input)
            }catch (e: Exception){
                throw ResolutionException("Failed to resolve '$input' as ${T::class.simpleName}: ${e.message}")
            }
        }
    }

    inline fun <reified T : Any, reified U : Any> addResolverExtra(noinline resolver: (String, U) -> T) {
        resolvers[T::class] = { input, extra ->
            checkNotNull(extra)
            if(extra is U) {
                try {
                    resolver(input, extra)
                } catch (e: Exception){
                    throw ResolutionException("Failed to resolve '$input' as ${T::class.simpleName}: ${e.message}")
                }
            }else{
                throw ResolutionException("Passed in wrong context for resolver type ${U::class.simpleName} (You passed in: ${extra::class.simpleName}")
            }
        }
    }

}
