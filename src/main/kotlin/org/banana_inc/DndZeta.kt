package org.banana_inc

import co.aikar.commands.annotation.Dependency
import org.banana_inc.commands.CommandManagement
import org.banana_inc.data.Database
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

open class DndZeta : JavaPlugin() {


    override fun onEnable() {
        plugin = this
        org.banana_inc.logger = logger
        Database.init()
        CommandManagement.registerCommands(this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun sum(a: Int, b: Int) = a + b
}

@Dependency
lateinit var plugin: DndZeta
lateinit var logger: Logger


