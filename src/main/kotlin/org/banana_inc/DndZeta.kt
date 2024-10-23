package org.banana_inc

import co.aikar.commands.annotation.Dependency
import com.zorbeytorunoglu.kLib.MCPlugin
import org.banana_inc.commands.CommandManagement

open class DndZeta : MCPlugin() {


    override fun onEnable() {
        // Plugin startup logic
        CommandManagement.registerCommands(this)
        instance = this
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun sum(a: Int, b: Int) = a + b
}

@Dependency
lateinit var instance: DndZeta
