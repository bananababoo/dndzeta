package org.banana_inc.inventory

import org.banana_inc.EventManager
import org.banana_inc.debug.DebugType
import org.banana_inc.debug.sendDebugMessage
import org.banana_inc.extensions.data
import org.banana_inc.extensions.syncInventory
import org.banana_inc.extensions.syncInventoryNextTick
import org.banana_inc.extensions.toItem
import org.banana_inc.logger
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.entity.HumanEntity
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerDropItemEvent
import kotlin.math.floor

@InitOnStartup
object ItemEvents {
    init {
        logger.info("Registering item events")
        EventManager.addListener<InventoryClickEvent> {
            val player = whoClicked
            if(player.data.localData.inGUI) return@addListener
            if(action == InventoryAction.NOTHING) return@addListener


            val dataInventory = whoClicked.data.inventory
            val slotItem = dataInventory[slot]

            if(clickedInventory != player.inventory){
                if(action == InventoryAction.MOVE_TO_OTHER_INVENTORY ) {
                    //possibly better way to do this, but that involves predicting where the items will go, and gets messy (:
                    // check no top inv -> if item in bottom row, distribute to top and the put the rest in the bottom.... (:::
                    //https://www.spigotmc.org/threads/predicting-the-results-of-an-inventoryinteractevent.618169/
                    player.inventory.syncInventoryNextTick {
                        sendDebugMessage(DebugType.INVENTORY,player,"Type: ${action}, Slot: ${slot}, Current Item: $slotItem")
                        sendDebugMessage(DebugType.INVENTORY,player,"<green>Old inventory: ${player.data.inventory}")
                        sendDebugMessage(DebugType.INVENTORY,player,"<blue>New inventory: ${player.data.inventory}")
                    }
                }
                return@addListener
            }
            sendDebugMessage(DebugType.INVENTORY,player,"<black>------------")


            sendDebugMessage(DebugType.INVENTORY,player,"Type: <red><bold>${action}, <reset><white>Slot: ${slot}, CurrentItem: $slotItem")
            sendDebugMessage(DebugType.INVENTORY,player,"<green>Old inventory: ${player.data.inventory}")

            when(action){
                InventoryAction.PICKUP_ALL,

                InventoryAction.PICKUP_HALF ->
                    slotItem?.let{ it.amount = floor(it.amount/2.0).toInt() }

                InventoryAction.PLACE_ALL,
                InventoryAction.PLACE_SOME, -> {
                    dataInventory[slot] = cursor.toItem.apply {
                        amount += dataInventory[slot]?.amount ?: 0
                    }
                }

                InventoryAction.PLACE_ONE -> {
                    dataInventory[slot] = cursor.toItem.apply {
                        amount = player.inventory.getItem(slot)?.amount?.plus(1) ?: 1
                    }
                }

                InventoryAction.SWAP_WITH_CURSOR -> {
                    dataInventory[slot] = cursor.toItem // if we swapping, there can't be some already there
                }

                InventoryAction.MOVE_TO_OTHER_INVENTORY -> {
                    //possibly better way to do this, but that involves predicting where the items will go, and gets messy (:
                    // check no top inv -> if item in bottom row, distribute to top and the put the rest in the bottom.... (:::
                    //https://www.spigotmc.org/threads/predicting-the-results-of-an-inventoryinteractevent.618169/
                    player.inventory.syncInventoryNextTick {
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
                    whoClicked.inventory.syncInventoryNextTick()
                }
                else -> Unit
            }
            sendDebugMessage(DebugType.INVENTORY,player,"<blue>New inventory: ${player.data.inventory}")
        }

        EventManager.addListener<InventoryDragEvent> {
            whoClicked.inventory.syncInventoryNextTick()
        }

        EventManager.addListener<InventoryCloseEvent>{
            player.inventory.syncInventory()
        }
        EventManager.addListener<InventoryCreativeEvent> {
            whoClicked.inventory.syncInventoryNextTick()
        }
        EventManager.addListener<EntityPickupItemEvent>{
            (entity as? HumanEntity)?.inventory?.syncInventoryNextTick()
        }
        EventManager.addListener<PlayerDropItemEvent>{
            player.inventory.syncInventoryNextTick()
        }
    }
}