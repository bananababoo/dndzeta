package org.banana_inc.item

import com.zorbeytorunoglu.kLib.task.delaySync
import org.banana_inc.EventManager
import org.banana_inc.extensions.data
import org.banana_inc.extensions.syncInventory
import org.banana_inc.extensions.toItem
import org.banana_inc.logger
import org.banana_inc.plugin
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import kotlin.math.ceil

@InitOnStartup
object ItemEvents {
    init {
        logger.info("Registering item events")
        EventManager.addListener<InventoryClickEvent> { clickEvent ->
            val dataInventory = clickEvent.whoClicked.data.inventory
            val slotItem = dataInventory[clickEvent.rawSlot]
            val cursor = clickEvent.cursor
            val player = clickEvent.whoClicked
            player.sendMessage("type: ${clickEvent.action}, slot: ${clickEvent.rawSlot}, item: $slotItem, cursor: $cursor")
            player.sendMessage("current inventory: ${player.data.inventory}")

            when(clickEvent.action){
                InventoryAction.PICKUP_ALL,
                InventoryAction.DROP_ALL_SLOT, ->
                    dataInventory.remove(clickEvent.rawSlot)

                InventoryAction.PICKUP_HALF ->
                    slotItem?.let{ it.amount = ceil(it.amount/2.0).toInt() }

                InventoryAction.DROP_ONE_SLOT -> {
                    slotItem?.let{
                        it.amount -= 1
                        if(it.amount == 0) dataInventory.remove(clickEvent.rawSlot)
                    }
                }

                InventoryAction.PLACE_ALL,
                InventoryAction.PLACE_SOME,
                InventoryAction.SWAP_WITH_CURSOR,
                InventoryAction.PLACE_ONE ->
                    dataInventory[clickEvent.rawSlot] = cursor.toItem

                InventoryAction.MOVE_TO_OTHER_INVENTORY -> {
                    //possibly better way to do this, but that involves predicting where the items will go, and gets messy (:
                    // check no top inv -> if item in bottom row, distribute to top and the put the rest in the bottom.... (:::
                    //https://www.spigotmc.org/threads/predicting-the-results-of-an-inventoryinteractevent.618169/
                    plugin.delaySync(1){
                        player.inventory.syncInventory()
                    }
                }

                InventoryAction.HOTBAR_SWAP -> {
                    val hotbarItem = dataInventory[clickEvent.hotbarButton + 27]
                    val currentItem = dataInventory[clickEvent.rawSlot]
                    dataInventory[clickEvent.rawSlot] = hotbarItem!!
                    dataInventory[clickEvent.hotbarButton + 27] = currentItem!!
                }

                InventoryAction.COLLECT_TO_CURSOR -> {
                    var i = cursor.amount
                    var j = 0;
                    while(i < cursor.maxStackSize || j > 35){
                        if(dataInventory[j] != null && dataInventory[j]!!.equalBesidesAmount(cursor.toItem)){
                            i += dataInventory[j]!!.amount
                            dataInventory.remove(i)
                        }
                        j++;
                    }
                }
                else -> Unit
            }
            player.sendMessage("end inventory: ${player.data.inventory}")

        }
    }
}