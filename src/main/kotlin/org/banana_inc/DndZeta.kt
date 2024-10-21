package org.banana_inc

import org.banana_inc.commands.Commands
import org.bukkit.plugin.java.JavaPlugin

open class DndZeta : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        getCommand("test")?.setExecutor(Commands.command1)


    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun sum(a: Int, b: Int) = a + b

}
