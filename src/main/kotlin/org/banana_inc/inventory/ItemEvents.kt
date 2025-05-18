package org.banana_inc.inventory

import org.banana_inc.EventManager
import org.banana_inc.data.Data
import org.banana_inc.debug.DebugType
import org.banana_inc.debug.sendDebugMessage
import org.banana_inc.extensions.*
import org.banana_inc.logger
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.entity.HumanEntity
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

@InitOnStartup
object ItemEvents {
    init {
        logger.info("Registering item events")
        EventManager.addListener<InventoryClickEvent> {
            logger.info("slot $slot")

            val player = whoClicked
            if(player.data.localData.inGUI) return@addListener
            if(action == InventoryAction.NOTHING) return@addListener

            if(clickedInventory == null) return@addListener
            val clickedInv = clickedInventory!!
            val dataInventory = clickedInv.getDataInventory()
            val slotItem = dataInventory[slot]

            if(clickedInv != player.inventory){
                if(action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    //possibly better way to do this, but that involves predicting where the items will go, and gets messy (:
                    // check no top inv -> if item in bottom row, distribute to top and the put the rest in the bottom.... (:::
                    //https://www.spigotmc.org/threads/predicting-the-results-of-an-inventoryinteractevent.618169/
                    player.inventory.syncInventoryToDataNextTick {
                        sendDebugMessage(DebugType.INVENTORY,player,"Type: ${action}, Slot: ${slot}, Current Item: $slotItem")
                        sendDebugMessage(DebugType.INVENTORY,player,"<green>Old inventory: ${player.data.inventory}")
                        sendDebugMessage(DebugType.INVENTORY,player,"<blue>New inventory: ${player.data.inventory}")
                    }

                }
                return@addListener
            }

            sendDebugMessage(DebugType.INVENTORY,player,"<black>------------")
            sendDebugMessage(DebugType.INVENTORY,player,"Type: <red><bold>${action}, <reset><white> Slot: ${slot}, CurrentItem: $slotItem")
            sendDebugMessage(DebugType.INVENTORY,player,"<green>Old inventory: ${player.data.inventory}")

            logger.info("type $action")
            when(action){
                InventoryAction.PICKUP_ALL -> {
                    dataInventory.remove(slot)
                }
                InventoryAction.PICKUP_HALF -> {
                    isCancelled = true
                    val amount = slotItem?.amount

                    slotItem?.amount = floor((amount?: 0) /2.0).toInt()
                    clickedInv.syncSlotFromData(slot)

                    val cursorItem = slotItem?.copy()
                    cursorItem?.amount = ceil((amount?: 0) /2.0).toInt()
                    player.setItemOnCursor(cursorItem?.itemStack())
                }

                InventoryAction.PLACE_ALL,
                InventoryAction.PLACE_SOME,
                -> {
                    val cursorItem = cursor.toItem
                    logger.info("slot amount: ${slotItem?.amount}, cursor amount ${cursorItem.amount}")
                    val total = (slotItem?.amount ?: 0) + cursorItem.amount
                    dataInventory[slot] = cursorItem.apply {
                        amount = min(total, cursorItem.type.stackSize)
                    }
                    if (total > cursorItem.type.stackSize) {
                        cursor.amount = total.mod(cursorItem.type.stackSize)
                    } else {
                        player.setItemOnCursor(null)
                    }
                    isCancelled = true
                    logger.info(dataInventory[slot].toString() + " after")
                    clickedInv.syncSlotFromData(slot)
                }

                InventoryAction.PLACE_ONE -> {
                    val cursorItem = cursor.toItem
                    logger.info("slotItem amount: ${slotItem?.amount}")
                    dataInventory[slot] = cursorItem.apply {
                        amount = (slotItem?.amount?: 0) + 1
                    }
                    clickedInv.syncSlotFromData(slot)
                    isCancelled = true
                    cursor.amount -= 1
                }

                InventoryAction.SWAP_WITH_CURSOR -> {
                    val cursorItem = cursor.toItem
                    val stackSize = cursorItem.type.stackSize
                    val clickedItem = clickedInv.getItem(slot)!!
                    if(slotItem?.equalBesidesAmount(cursorItem) == true){
                        val addAmount = if(click.isRightClick) 1 else cursorItem.amount
                        val total = cursorItem.amount + clickedItem.amount

                        if(click.isRightClick && clickedItem.amount < stackSize){
                            cursor.amount -= 1
                        }
                        if(click.isLeftClick) {
                            cursor.amount = if (total > stackSize) (total).mod(stackSize) else 0
                        }

                        dataInventory[slot] = cursorItem.apply {
                            amount = min((clickedItem.amount + addAmount), slotItem.type.stackSize)
                        }
                        clickedInv.syncSlotFromData(slot)
                        isCancelled = true // so the item that we added to the stack isn't swapped and our cursor is empty
                    }else {
                        dataInventory[slot] = cursor.toItem // if we're swapping, there can't be some already there
                    }
                }

                InventoryAction.MOVE_TO_OTHER_INVENTORY -> {
                    //possibly better way to do this, but that involves predicting where the items will go, and gets messy (:
                    // check no top inv -> if item in bottom row, distribute to top and the put the rest in the bottom.... (:::
                    //https://www.spigotmc.org/threads/predicting-the-results-of-an-inventoryinteractevent.618169/
                    clickedInv.syncInventoryToDataNextTick {
                        sendDebugMessage(DebugType.INVENTORY,player,"<blue>New inventory: ${player.data.inventory}")
                    }
                }

                InventoryAction.HOTBAR_SWAP -> {
                    val hotbarItem = dataInventory[hotbarButton + 27]
                    val currentItem = dataInventory[slot]
                    dataInventory[slot] = hotbarItem!!
                    dataInventory[hotbarButton + 27] = currentItem!!
                }

                InventoryAction.COLLECT_TO_CURSOR -> {
                    collectItems(dataInventory, cursor)
                    clickedInv.syncDataToInventory()
                    isCancelled = true
                }
                else -> Unit
            }
            sendDebugMessage(DebugType.INVENTORY,player,"<blue>New inventory: ${player.data.inventory}")
        }

        EventManager.addListener<InventoryDragEvent> {
            whoClicked.inventory.syncInventoryToDataNextTick()
        }

        EventManager.addListener<InventoryCloseEvent>{
            player.inventory.syncInventoryToData()
        }
        EventManager.addListener<InventoryCreativeEvent> {
            whoClicked.inventory.syncInventoryToDataNextTick()
        }
        EventManager.addListener<EntityPickupItemEvent>{
            isCancelled = true
            val entity = entity //kotlin smart casting don't work with properties
            if(entity is InventoryHolder){
                if(entity is HumanEntity) {
                    entity.inventory.addItemStackCorrectly(item.itemStack, entity.inventory.heldItemSlot) //TODO what happens if can't pick up all items???
                }else{
                    entity.inventory.addItemStackCorrectly(item.itemStack)
                }
            }
            (entity as? HumanEntity)?.inventory?.syncInventoryToDataNextTick()
            item.remove()
            //ENCHANCEMENT: Play pickup sound
        }
        EventManager.addListener<PlayerDropItemEvent>{
            player.inventory.syncInventoryToDataNextTick()
        }
    }

    private fun collectItems(inv: Data.Inventory, cursor: ItemStack){
        var slot = 8
        var rotation = 0
        val item = cursor.toItem
        //todo return if stack size == 1???
        while(cursor.amount <= item.type.stackSize){
            slot += 1
            if(slot > 35) slot = 0
            if(slot == 9) rotation++
            if(rotation == 3) return

            val slotItem = inv.items[slot]

            if(slotItem == null || !slotItem.equalBesidesAmount(item)) continue
            if(rotation == 1 && slotItem.amount == slotItem.type.stackSize) continue


            if (cursor.amount + slotItem.amount <= item.type.stackSize){
                cursor.amount += slotItem.amount
                inv.remove(slot)
                logger.info("removed $slot")
            }else{
                slotItem.amount -= (item.type.stackSize - cursor.amount)
                logger.info("final amount: $slot")
                cursor.amount = item.type.stackSize
                return
            }

        }
    }
}