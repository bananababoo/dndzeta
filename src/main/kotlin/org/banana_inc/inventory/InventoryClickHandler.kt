package org.banana_inc.inventory

import org.banana_inc.EventManager
import org.banana_inc.data.Data
import org.banana_inc.debug.DebugType
import org.banana_inc.debug.sendDebugMessage
import org.banana_inc.extensions.*
import org.banana_inc.item.Item
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

@InitOnStartup
object InventoryClickHandler {
    init {
        registerInventoryClickEvent()
    }

    private fun registerInventoryClickEvent(){
        EventManager.addListener<InventoryClickEvent> {
            val player = whoClicked

            if(player.data.localData.inGUI || action == InventoryAction.NOTHING || clickedInventory == null){
                return@addListener
            }
            val clickedInv = clickedInventory!!
            val dataInventory = clickedInv.getDataInventory()
            val slotItem = dataInventory[slot]

            if(!handleExternal(player,clickedInv, action, slot, slotItem)) return@addListener

            ItemEvents.logInventoryDebug(player, action, slot, slotItem)

            when(action){
                InventoryAction.PICKUP_ALL -> pickupAll(dataInventory, slot)
                InventoryAction.PICKUP_HALF -> {
                    isCancelled = true
                    pickupHalf(slotItem, clickedInv, slot, player)
                }
                InventoryAction.PLACE_ALL, InventoryAction.PLACE_SOME, -> {
                    isCancelled = true
                    placeAll(cursor, slotItem, dataInventory, slot, player, clickedInv)
                }
                InventoryAction.PLACE_ONE -> {
                    isCancelled = true
                    placeOne(cursor, dataInventory, slot, slotItem, clickedInv)
                }
                InventoryAction.SWAP_WITH_CURSOR -> {
                    isCancelled = true
                    swapWithCursor(cursor, clickedInv, slot, slotItem, click, dataInventory)
                }
                InventoryAction.MOVE_TO_OTHER_INVENTORY -> moveToOtherInventory(clickedInv, player)
                InventoryAction.HOTBAR_SWAP -> hotbarSwap(dataInventory, slot, hotbarButton, slotItem)
                InventoryAction.COLLECT_TO_CURSOR -> {
                    isCancelled = true
                    collectToCursor(dataInventory, cursor, clickedInv)
                }
                else -> Unit
            }
            sendDebugMessage(DebugType.INVENTORY,player,"<blue>New inventory: ${player.data.inventory}")
        }
    }

    private fun pickupAll(dataInventory: Data.Inventory, slot: Int){
        dataInventory.remove(slot)
    }

    private fun pickupHalf(slotItem: Item<*>?, clickedInv: Inventory, slot: Int, player: HumanEntity){
        val amount = slotItem?.amount

        slotItem?.amount = floor((amount?: 0) /2.0).toInt()
        clickedInv.syncSlotFromData(slot)

        val cursorItem = slotItem?.copy()
        cursorItem?.amount = ceil((amount?: 0) /2.0).toInt()
        player.setItemOnCursor(cursorItem?.itemStack())
    }

    private fun placeAll(cursor: ItemStack, slotItem: Item<*>?, dataInventory: Data.Inventory, slot: Int, player: HumanEntity, clickedInv: Inventory){
        val cursorItem = cursor.toItem
        val total = (slotItem?.amount ?: 0) + cursorItem.amount
        dataInventory[slot] = cursorItem.apply {
            amount = min(total, cursorItem.type.stackSize)
        }
        if (total > cursorItem.type.stackSize) {
            cursor.amount = total - cursorItem.type.stackSize
        } else {
            player.setItemOnCursor(null)
        }
        clickedInv.syncSlotFromData(slot)
    }

    private fun placeOne(cursor: ItemStack, dataInventory: Data.Inventory, slot: Int, slotItem: Item<*>?, clickedInv: Inventory){
        val cursorItem = cursor.toItem
        dataInventory[slot] = cursorItem.apply {
            amount = (slotItem?.amount?: 0) + 1
        }
        cursor.amount -= 1
        clickedInv.syncSlotFromData(slot)
    }

    private fun swapWithCursor(cursor: ItemStack, clickedInv: Inventory, slot: Int, slotItem: Item<*>?, click: ClickType, dataInventory: Data.Inventory){
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
                cursor.amount = if (total > stackSize) total - stackSize else 0
            }

            dataInventory[slot] = cursorItem.apply {
                amount = min((clickedItem.amount + addAmount), slotItem.type.stackSize)
            }
            clickedInv.syncSlotFromData(slot)
        } else {
            dataInventory[slot] = cursor.toItem // if we're swapping, there can't be some already there
        }
    }

    private fun moveToOtherInventory(clickedInv: Inventory, player: HumanEntity){
        //possibly better way to do this, but that involves predicting where the items will go, and gets messy (:
        // check no top inv -> if item in bottom row, distribute to top and the put the rest in the bottom.... (:::
        //https://www.spigotmc.org/threads/predicting-the-results-of-an-inventoryinteractevent.618169/
        clickedInv.syncInventoryToDataNextTick {
            sendDebugMessage(DebugType.INVENTORY,player,"<blue>New inventory: ${player.data.inventory}")
        }
    }

    private fun collectToCursor(dataInventory: Data.Inventory, cursor: ItemStack, clickedInv: Inventory){
        collectItems(dataInventory, cursor)
        clickedInv.syncDataToInventory()
    }

    private fun handleExternal(player: HumanEntity, clickedInv: Inventory, action: InventoryAction, slot: Int, slotItem: Item<*>?): Boolean {
        if(clickedInv != player.inventory){
            if(action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                player.inventory.syncInventoryToDataNextTick {
                    ItemEvents.logInventoryDebug(player, action, slot, slotItem)
                }
            }
            return false
        }
        return true
    }

    private fun hotbarSwap(dataInventory: Data.Inventory, slot: Int, hotbarButton: Int, slotItem: Item<*>?){
        val hotbarItem = dataInventory[hotbarButton + 27]
        val currentItem = slotItem
        dataInventory[slot] = hotbarItem!!
        dataInventory[hotbarButton + 27] = currentItem!!
    }

    private fun collectItems(inv: Data.Inventory, cursor: ItemStack){
        var slot = 8
        var rotation = 0
        val item = cursor.toItem
        while(cursor.amount <= item.type.stackSize){
            slot += 1
            if(slot > 35) slot = 0
            if(slot == 9) rotation++
            if(rotation == 3) return

            val slotItem = inv[slot]

            if(slotItem == null || !slotItem.equalBesidesAmount(item)) continue
            if(rotation == 1 && slotItem.amount == slotItem.type.stackSize) continue

            if (cursor.amount + slotItem.amount <= item.type.stackSize){
                cursor.amount += slotItem.amount
                inv.remove(slot)
            } else {
                slotItem.amount -= (item.type.stackSize - cursor.amount)
                cursor.amount = item.type.stackSize
                return
            }
        }
    }
}
