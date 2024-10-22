package org.banana_inc

import com.zorbeytorunoglu.kLib.MCPlugin
import org.banana_inc.commands.Commands

open class DndZeta : MCPlugin() {

    private lateinit var commands: Commands

    override fun onEnable() {
        // Plugin startup logic
        commands = Commands(this)
        getCommand("test")?.setExecutor(commands.command1)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun sum(a: Int, b: Int) = a + b
}
