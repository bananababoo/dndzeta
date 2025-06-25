package org.banana_inc.item.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Subcommand
import org.banana_inc.EventManager.chatCallback
import org.banana_inc.extensions.*
import org.banana_inc.item.Item
import org.banana_inc.item.Modifier
import org.banana_inc.util.reflection.ClassGraph
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

@CommandAlias("item_modifier|im")
object Modifier: BaseCommand() {
    val allModifiers = ClassGraph.allSubClasses<Modifier<*>>()

    @Subcommand("get")
    fun get(p: Player) {
        if(p.hasItemInHand) {
            val item = p.itemInMainHand!!
            sendMessage(p, "${item.getModifiers()}")
        }
    }

    @Subcommand("set")
    @CommandCompletion("@modifiers @nothing")
    fun set(p: Player, modifierClass: String) {
        if(p.hasItemInHand) {
            val modifierClass =
                allModifiers.find { it.simpleName == modifierClass } ?: return sendError(p, "Invalid Modifier")
            val item = p.data.inventory[p.inventory.heldItemSlot] ?: return sendError(p, "Invalid Item")
            addModifierChat(modifierClass, p, item, p.inventory, p.inventory.heldItemSlot)
        }
    }

    fun addModifierChat(modifierClazz: KClass<out Modifier<*>>, player: Player, item: Item<*>, inventory: Inventory, slot: Int){
        if(modifierClazz.primaryConstructor == null){
            item.addModifier(modifierClazz.objectInstance!!)
            inventory.syncSlotFromData(slot)
            sendMessage(player, "Added modifier ${modifierClazz.simpleName}")
            return
        }
        sendMessage(player, "Adding modifier ${modifierClazz.simpleName}")
        val constructor = modifierClazz.primaryConstructor!!
        val args = mutableSetOf<Any>()
        var depth = 0
        lateinit var parameterValueAsker: () -> Unit

        parameterValueAsker = question@{
            if(constructor.valueParameters.size == depth) {
                val modifier = constructor.call(*args.toTypedArray())
                item.addModifier(modifier)
                inventory.syncSlotFromData(slot)
                sendMessage(player, "Added modifier $modifier")
                return@question
            }
            val parameter = constructor.valueParameters[depth]
            val value = parameter.type.classifier as KClass<*>
            sendMessage(player, "Input a ${parameter.name} of type ${value.simpleName} ")
            player.chatCallback(value) {
                args.add(it)
                depth += 1
                sendMessage(player, "$depth depth and name")
                parameterValueAsker()
            }
        }
        parameterValueAsker()
    }
}