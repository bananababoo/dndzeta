package org.banana_inc.commands

import co.aikar.commands.PaperCommandManager
import org.banana_inc.logger
import org.banana_inc.plugin
import org.banana_inc.util.initialization.InitOnStartup
import org.banana_inc.util.reflection.ClassGraph

@InitOnStartup
object CommandManagement{

    private var manager: PaperCommandManager = PaperCommandManager(plugin)

    init {
        for (clazz in ClassGraph.allCommandClasses) {
            if(clazz.objectInstance == null) continue
            manager.registerCommand(clazz.objectInstance)
            logger.info("Registered command ${clazz.simpleName}")
        }
    }

}