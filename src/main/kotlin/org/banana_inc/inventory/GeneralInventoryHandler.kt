package org.banana_inc.inventory

import org.banana_inc.EventManager
import org.banana_inc.extensions.syncInventoryToData
import org.banana_inc.extensions.syncInventoryToDataNextTick
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.inventory.InventoryDragEvent

@InitOnStartup
object GeneralInventoryHandler{
    init {
        registerItemEvents()
    }

    private fun registerItemEvents(){
        EventManager.addListener<InventoryDragEvent> {
            whoClicked.inventory.syncInventoryToDataNextTick()
        }
        EventManager.addListener<InventoryCloseEvent> {
            if (player is Player) { // cause there's the possibility a npc closes their inventory lmao
                if ((player as Player).isOnline) { // theres a chance invclose gets called AFTER playerdate gets unloaded and playerdata gets saved
                    player.inventory.syncInventoryToData()
                } else return@addListener
            } else player.inventory.syncInventoryToData()
        }
        EventManager.addListener<InventoryCreativeEvent> {
            whoClicked.inventory.syncInventoryToDataNextTick()
        }
    }
}