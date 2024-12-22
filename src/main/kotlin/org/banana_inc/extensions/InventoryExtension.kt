package org.banana_inc.extensions

import org.banana_inc.item.Item
import org.banana_inc.logger
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

fun PlayerInventory.addItem(item: Item<*>) {
    val newItem = item.itemStack()
    this.addItem(newItem)
    logger.info("Added $newItem to inventory")
    logger.info("inventroy: $this")

    syncInventory()
}

fun PlayerInventory.syncInventory(){
    holder!!.sendMessage("synching")
    val playerData = (holder!! as Player).data
    for(i in contents.indices){
        if(contents[i] == null) playerData.inventory.remove(i)
        val item = contents[i]!!.toItem
        playerData.inventory[i] = item
    }
}

fun PlayerInventory.containsItem(item: Item<*>) = this.contains(item.itemStack())