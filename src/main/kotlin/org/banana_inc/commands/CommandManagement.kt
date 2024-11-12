package org.banana_inc.commands

import co.aikar.commands.PaperCommandManager
import org.banana_inc.plugin
import org.banana_inc.util.initialization.InitOnStartup

@InitOnStartup
object CommandManagement{

    private var manager: PaperCommandManager = PaperCommandManager(plugin)

    init {
        manager.registerCommand(Command())
    }

}