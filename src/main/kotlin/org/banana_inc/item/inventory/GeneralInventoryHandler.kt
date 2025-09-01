package org.banana_inc.item.inventory

import org.banana_inc.extensions.syncInventoryToData
import org.banana_inc.extensions.syncInventoryToDataNextTick
import org.banana_inc.on
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
        on<InventoryDragEvent> {
            whoClicked.inventory.syncInventoryToDataNextTick()
        }

        on<InventoryCloseEvent> {
            if (player is Player) { // cause there's the possibility a npc closes their inventory lmao
                if ((player as Player).isOnline) { // theres a chance invclose gets called AFTER playerdate gets unloaded and playerdata gets saved
                    player.inventory.syncInventoryToData()
                } else return@on
            } else player.inventory.syncInventoryToData()
        }

        on<InventoryCreativeEvent> {
            whoClicked.inventory.syncInventoryToDataNextTick()
        }
    }
}