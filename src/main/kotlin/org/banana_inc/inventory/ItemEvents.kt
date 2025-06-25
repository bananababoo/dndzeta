package org.banana_inc.inventory

import org.banana_inc.debug.DebugType
import org.banana_inc.debug.sendDebugMessage
import org.banana_inc.extensions.data
import org.banana_inc.item.Item
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryAction


object ItemEvents {

    fun logInventoryDebug(player: HumanEntity, action: InventoryAction, slot: Int, slotItem: Item<*>?) {
        sendDebugMessage(DebugType.INVENTORY, player, "<black>------------")
        sendDebugMessage(DebugType.INVENTORY, player, "Type: <red><bold>${action}, <reset><white> Slot: ${slot}, CurrentItem: $slotItem")
        sendDebugMessage(DebugType.INVENTORY, player, "<green>Old inventory: ${player.data.inventory}")
    }

}