package org.banana_inc.inventory

import org.banana_inc.EventManager
import org.banana_inc.extensions.addItemStackCorrectly
import org.banana_inc.extensions.syncInventoryToDataNextTick
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.entity.HumanEntity
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.InventoryHolder

@InitOnStartup
object PickupDropHandler {
    init {
        registerItemPickupAndDropEvents()
    }

    private fun registerItemPickupAndDropEvents(){
        EventManager.addListener<EntityPickupItemEvent>{
            isCancelled = true
            val entity = entity //kotlin smart casting don't work with properties
            if(entity is InventoryHolder){
                if(entity is HumanEntity) {
                    entity.inventory.addItemStackCorrectly(item.itemStack, entity.inventory.heldItemSlot) //TODO what happens if can't pick up all items???
                }else{
                    entity.inventory.addItemStackCorrectly(item.itemStack)
                }
                entity.inventory.syncInventoryToDataNextTick()
            }

            item.remove()
            //TODO: Play pickup sound
        }
        EventManager.addListener<PlayerDropItemEvent>{
            player.inventory.syncInventoryToDataNextTick()
        }
    }
}