package org.banana_inc.extensions

import com.zorbeytorunoglu.kLib.task.nextTick
import org.banana_inc.debug.DebugType
import org.banana_inc.debug.sendDebugMessage
import org.banana_inc.item.Item
import org.banana_inc.logger
import org.banana_inc.plugin
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

fun PlayerInventory.addItem(vararg items: Item<*>, sync: Boolean=true) {
    for(item in items) {
        val newItem = item.itemStack()
        this.addItem(newItem)
        logger.info("Added $newItem to inventory")
        logger.info("inventroy: $this")
    }
    if(sync) {
        syncInventory()
    }
}

fun PlayerInventory.syncInventory(){
    sendDebugMessage(DebugType.INVENTORY,holder!!,"<blue>Syncing")
    val playerData = (holder!! as Player).data
    playerData.inventory.clear()

    for(i in contents.indices){
        playerData.inventory[i] = contents[i]?.toItem ?: continue
    }
}

fun PlayerInventory.syncInventoryNextTick(){
    plugin.nextTick {
        syncInventory()
    }
}

fun PlayerInventory.syncInventoryNextTick(then: () -> Unit){
    plugin.nextTick {
        syncInventory()
        then()
    }
}

fun PlayerInventory.containsItem(item: Item<*>) = this.contains(item.itemStack())