package org.banana_inc.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import org.banana_inc.data.PlayerData
import org.banana_inc.extensions.data
import org.banana_inc.extensions.getFirstClassTypeArgument
import org.banana_inc.extensions.hasItemInHand
import org.banana_inc.extensions.itemInMainHand
import org.banana_inc.item.Modifier
import org.banana_inc.item.commands.ModifierCommand
import org.banana_inc.logger
import org.banana_inc.plugin
import org.banana_inc.util.initialization.InitOnStartup
import org.banana_inc.util.reflection.ClassGraph
import kotlin.reflect.full.isSubclassOf

@InitOnStartup
object CommandManagement{

    private var manager: PaperCommandManager = PaperCommandManager(plugin)

    init {
        manager.commandContexts.registerContext(PlayerData::class.java){
            it.player.data
        }

        addItemCompletions()
        addItemContexts()

        for (clazz in ClassGraph.allSubClasses<BaseCommand>()) {
            if(clazz.objectInstance == null) continue
            manager.registerCommand(clazz.objectInstance)
            logger.info("Registered command ${clazz.simpleName}")
        }
    }

    fun addItemCompletions(){
        addModifierCompletions()
    }

    fun addItemContexts(){
        addModifierContexts()
    }

    fun addModifierCompletions(){
        manager.commandCompletions.registerAsyncCompletion("modifiers"){ it ->
            if(it.player.hasItemInHand) {
                return@registerAsyncCompletion ModifierCommand.allModifiers.filter { i ->
                    val modSpecifier = i.getFirstClassTypeArgument
                    val mainHand = it.player.itemInMainHand?: return@filter false
                    val itemType = mainHand.type::class
                    itemType == modSpecifier || itemType.isSubclassOf(modSpecifier)
                }.map { it.simpleName }
            }
            return@registerAsyncCompletion null
        }
        manager.commandCompletions.registerAsyncCompletion("existingModifiers"){ it ->
            if(it.player.hasItemInHand) {
                return@registerAsyncCompletion it.player.itemInMainHand!!.getModifiersCopy().map { it::class.java.simpleName }
            }
            return@registerAsyncCompletion null
        }
    }

    fun addModifierContexts(){
        manager.commandContexts.registerContext(Modifier::class.java){ it ->
            if(it.player.hasItemInHand) {
                val name = it.popFirstArg()
                return@registerContext it.player.itemInMainHand!!.getModifiersCopy().filter { it::class.simpleName == name }.getOrNull(0)
            }
            return@registerContext null
        }
    }
}