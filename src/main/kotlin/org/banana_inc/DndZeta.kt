package org.banana_inc

import co.aikar.commands.annotation.Dependency
import org.banana_inc.util.reflection.ClassGraph
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

open class DndZeta : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        @SuppressWarnings("redundantQualifier")
        org.banana_inc.logger = logger
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


