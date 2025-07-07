package org.banana_inc.item.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Subcommand
import org.banana_inc.EventManager
import org.banana_inc.EventManager.chatCallback
import org.banana_inc.chat.ComplexTypedMultiLineEditor
import org.banana_inc.chat.MultiLineEditor
import org.banana_inc.extensions.*
import org.banana_inc.item.Item
import org.banana_inc.item.Modifier
import org.banana_inc.item.MultiModifier
import org.banana_inc.util.readable
import org.banana_inc.util.reflection.ClassGraph
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

@CommandAlias("item_modifier|im")
object ModifierCommand: BaseCommand() {
    val allModifiers = ClassGraph.allSubClasses<Modifier<*>>()

    @Subcommand("get")
    fun get(player: Player) {
        if(player.hasItemInHand) {
            val item = player.itemInMainHand!!
            sendMessage(player, "${item.getModifiersCopy()}")
        }
    }

    @Subcommand("set")
    @CommandCompletion("@modifiers @nothing")
    fun set(player: Player, modifierClassName: String) {
        if(player.hasItemInHand) {
            val modifierClass = allModifiers.find { it.simpleName == modifierClassName }
                    ?: return sendError(player, "Invalid Modifier1")
            val item = player.data.inventory[player.inventory.heldItemSlot]
                ?: return sendError(player, "Invalid Item")
            if(!item.checkIfModifierValid(modifierClass)) return sendError(player, "Invalid Modifier")

            ModifierChatHandler(player, player.inventory.heldItemSlot)
                .startModifierChat(modifierClass, player, item, player.inventory)
        }
    }

    @Subcommand("remove")
    @CommandCompletion("@existingModifiers @nothing")
    fun remove(player: Player, modifier: Modifier<*>){
        if(player.hasItemInHand){
            player.itemInMainHand!!.removeModifier(modifier)
            player.inventory.syncSlotFromData(player.inventory.heldItemSlot)
        }
    }

    class ModifierChatHandler(val player: Player, val slot: Int) {

        private lateinit var lockSlot: List<EventManager.Removable>

        fun startModifierChat(modifierClazz: KClass<out Modifier<*>>, player: Player, item: Item<*>, inventory: Inventory) {
            sendMessage(player, "Added modifier ", modifierClazz)
            if (modifierClazz.primaryConstructor == null) {
                item.addModifier(modifierClazz.objectInstance!!)
                inventory.syncSlotFromData(player.inventory.heldItemSlot)
                return
            }

            lockSlot = setupSlotLock() // lock the slot

            if (modifierClazz.isSubclassOf(MultiModifier::class)) {
                @Suppress("unchecked_cast")
                openMultiModifierEditor(modifierClazz as KClass<out MultiModifier<*, *>>,
                    modifierClazz.getClassTypeArgument(1), item, player)
            } else askModifierQuestions(modifierClazz, item, player)
        }

        @Suppress("unchecked_cast")
        fun <T : Any> openMultiModifierEditor(
            modifierClazz: KClass<out MultiModifier<*, *>>, listType: KClass<T>, item: Item<*>, player: Player) {
            val existingModifier = item.getModifiersCopy().find { it is MultiModifier<*, *> && it::class == modifierClazz }
            val modifier: MultiModifier<*, T>
            val modifierExists = existingModifier != null

            modifier = if (modifierExists)
                existingModifier as MultiModifier<*, T>
            else
                (modifierClazz as KClass<out MultiModifier<*, T>>).primaryConstructor!!.call(mutableListOf<T>())

            val afterEditCallback = fun MultiLineEditor<T>.() {
                if (!modifierExists) item.addModifier(modifier)
                modifier.list = lines.toMutableList()
                player.data.inventory[slot] = item
                player.inventory.syncDataToInventory()
                lockSlot.forEach { it.remove() }
                sendMessage(player, "Modifiers: ${item.getModifiersCopy()}")
            }

            if (modifier.complex) {
                ComplexTypedMultiLineEditor(player, listType, modifier.list, afterEditCallback)
            } else {
                MultiLineEditor(player, listType, modifier.list, afterEditCallback)
            }
        }

        fun askModifierQuestions(modifierClazz: KClass<out Modifier<*>>, item: Item<*>, player: Player) {
            val constructor = modifierClazz.primaryConstructor!!
            val args = mutableSetOf<Any>()
            var parameterIndex = 0
            lateinit var parameterValueAsker: () -> Unit

            parameterValueAsker = question@{
                if (constructor.valueParameters.size == parameterIndex) {
                    val modifier = constructor.call(*args.toTypedArray())
                    item.addModifier(modifier)
                    player.data.inventory[slot] = item
                    player.inventory.syncSlotFromData(player.inventory.heldItemSlot)
                    lockSlot.forEach { it.remove() }
                    sendMessage(player, "<gray>Added modifier <green>$modifier")
                    return@question
                }
                val parameter = constructor.valueParameters[parameterIndex]
                val parameterClass = parameter.type.classifier as KClass<*>
                sendMessage(
                    player,
                    "<gray>Input a <aqua>${parameter.name}<gray> of type <gold>".component.append(parameterClass.readable))
                player.chatCallback(parameterClass) {
                    args.add(it)
                    parameterIndex += 1
                    parameterValueAsker()
                }
            }
            parameterValueAsker()
        }

        private fun setupSlotLock(): MutableList<EventManager.Removable> {
            val events: MutableList<EventManager.Removable> = mutableListOf()
            events.add(EventManager.addRemovableListener<InventoryClickEvent> {
                if (this@ModifierChatHandler.player.inventory == this.clickedInventory && slot == this@ModifierChatHandler.slot) {
                    isCancelled = true
                }
            })
            events.add(EventManager.addRemovableListener<InventoryDragEvent> {
                if (this@ModifierChatHandler.player.inventory == this.inventory && inventorySlots.contains(this@ModifierChatHandler.slot)) {
                    isCancelled = true
                }
            })
            events.add(EventManager.addRemovableListener<PlayerDropItemEvent> {
                if (this@ModifierChatHandler.player == this.player && slot == player.inventory.heldItemSlot) {
                    isCancelled = true
                }
            })
            return events
        }
    }

}