package org.banana_inc.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import org.banana_inc.data.Data
import org.banana_inc.extensions.data
import org.banana_inc.extensions.hasItemInHand
import org.banana_inc.extensions.itemInMainHand
import org.banana_inc.item.commands.Modifier
import org.banana_inc.logger
import org.banana_inc.plugin
import org.banana_inc.util.initialization.InitOnStartup
import org.banana_inc.util.reflection.ClassGraph
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@InitOnStartup
object CommandManagement{

    private var manager: PaperCommandManager = PaperCommandManager(plugin)

    init {
        manager.commandContexts.registerContext(Data.Player::class.java){
            it.player.data
        }

        addItemCompletions()

        for (clazz in ClassGraph.allSubClasses<BaseCommand>()) {
            if(clazz.objectInstance == null) continue
            manager.registerCommand(clazz.objectInstance)
            logger.info("Registered command ${clazz.simpleName}")
        }
    }

    fun addItemCompletions(){
        addModifierCompletions()
    }


    fun addModifierCompletions(){
        manager.commandCompletions.registerAsyncCompletion("modifiers"){ it ->
            if(it.player.hasItemInHand) {
                return@registerAsyncCompletion Modifier.allModifiers.filter { i ->
                    val modSpecifier = i.supertypes.first().arguments.first().type!!.classifier as KClass<*>
                    val itemType = it.player.itemInMainHand!!.type::class
                    itemType == modSpecifier || itemType.isSubclassOf(modSpecifier)
                }.map { it.simpleName }
            }
            return@registerAsyncCompletion null
        }
    }
}