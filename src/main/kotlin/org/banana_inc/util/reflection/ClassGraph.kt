package org.banana_inc.util.reflection

import io.github.classgraph.ClassGraph
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityAirChangeEvent
import org.bukkit.event.inventory.HopperInventorySearchEvent
import org.bukkit.event.vehicle.VehicleUpdateEvent
import kotlin.reflect.KClass

object ClassGraph {

    private val ignoredEvents: List<KClass<out Event>> = listOf(
        EntityAirChangeEvent::class,
        HopperInventorySearchEvent::class,
        VehicleUpdateEvent::class
    )

    // Lazy property for all bukkit event classes
    val allBukkitEventClasses: List<Class<out Event>> by lazy {
        val eventClasses = mutableListOf<Class<out Event>>()
        ClassGraph()
            .enableClassInfo()
            .enableExternalClasses()
            .enableAnnotationInfo() // This may help in cases where Paper or Spigot jars are external
            .acceptPackages(
                "org.bukkit.event",
                "com.destroystokyo.paper.event",
                "io.papermc.paper.event"
            ) // Include Paper and Spigot packages if needed
            .scan().use { result ->
                result.getSubclasses(Event::class.java.name).forEach { classInfo ->
                    val clazz = classInfo.loadClass(Event::class.java)
                    if ((!ignoredEvents.contains(clazz.kotlin))
                        && !java.lang.reflect.Modifier.isAbstract(clazz.modifiers)
                        && classInfo.getAnnotationInfo(Deprecated::class.java) == null
                    ) { // Only include non-abstract classes
                        eventClasses.add(clazz)
                    }
                }
            }
        eventClasses
    }

    // Lazy property for init-on-startup classes
    val initOnStartupClasses: List<KClass<out Any>> by lazy {
        val eventClasses = mutableListOf<KClass<out Any>>()
        ClassGraph()
            .enableClassInfo()
            .enableExternalClasses()
            .enableAnnotationInfo()
            .acceptPackages(
                "org.banana_inc",
            )
            .scan().use { result ->
                result.getClassesWithAnnotation(InitOnStartup::class.java).forEach { classInfo ->
                    val clazz = classInfo.loadClass(Any::class.java).kotlin
                    eventClasses.addAll(allSubClasses(clazz))
                }
            }
        eventClasses
    }

    fun allSubClasses(first: KClass<out Any>,list: MutableList<KClass<out Any>> = mutableListOf()): MutableList<KClass<out Any>>{
        list.add(first)
        for(i in first.sealedSubclasses) allSubClasses(i,list)
        return list
    }

}
