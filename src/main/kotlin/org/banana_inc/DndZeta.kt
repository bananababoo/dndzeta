package org.banana_inc

import co.aikar.commands.annotation.Dependency
import com.google.gson.Gson
import org.banana_inc.util.reflection.ClassGraph
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

open class DndZeta : JavaPlugin() {

    val gson = Gson()

    override fun onEnable() {
        plugin = this
        (logger.also { org.banana_inc.logger = it })
        for (initOnStartupClass in ClassGraph.initOnStartupClasses) {
            initOnStartupClass.objectInstance
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun sum(a: Int, b: Int) = a + b
}

@Dependency
lateinit var plugin: DndZeta
lateinit var logger: Logger


