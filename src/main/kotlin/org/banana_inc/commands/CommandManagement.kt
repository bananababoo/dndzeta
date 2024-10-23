package org.banana_inc.commands

import co.aikar.commands.PaperCommandManager
import org.banana_inc.DndZeta
import org.banana_inc.util.initialization.RegistrationLock

object CommandManagement{

    lateinit var manager: PaperCommandManager

    fun registerCommands(plugin: DndZeta) {
        RegistrationLock.register(this)

        manager = PaperCommandManager(plugin)
        manager.registerCommand(Command())

    }

}