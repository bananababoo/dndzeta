package org.banana_inc.extensions

import com.zorbeytorunoglu.kLib.task.nextTick
import org.banana_inc.data.Data
import org.banana_inc.debug.DebugType
import org.banana_inc.debug.sendDebugMessage
import org.banana_inc.item.Item
import org.banana_inc.logger
import org.banana_inc.plugin
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

fun Inventory.addItem(vararg items: Item<*>) {
    for(item in items) {
        val newItem = item.itemStack()
        if(this.type == InventoryType.PLAYER){
            logger.info("player inventory pickup ${(this as PlayerInventory).heldItemSlot}")
            this.addItemStackCorrectly(newItem, heldSlot = this.heldItemSlot)
        }else{
            this.addItemStackCorrectly(newItem)
        }
        logger.info("Added $newItem to inventory")
        logger.info("inventroy: $this")
    }
    syncInventoryToData()
}

fun Inventory.addItemStackCorrectly(newItem: ItemStack, heldSlot: Int = 0): ItemStack? {
    logger.info("heldslot $heldSlot")
    val item = newItem.toItem
    for (slot in (heldSlot..<heldSlot + 1) + (0..35)) {
        if (newItem.amount <= 0) break

        logger.info("slot!!! $slot")
        val slotItem = getDataInventory().items[slot]

        if (slotItem == null || !slotItem.equalBesidesAmount(item)) continue
        if (slotItem.amount == slotItem.type.stackSize) continue

        if (newItem.amount + slotItem.amount <= item.type.stackSize) {
            slotItem.amount += newItem.amount
            newItem.amount = 0
        } else {
            newItem.amount -= (item.type.stackSize - slotItem.amount)
            slotItem.amount = item.type.stackSize
        }
    }

    for (slot in (0..35)) {
        if (newItem.amount <= 0) break
        val slotItem = getDataInventory().items[slot]
        if(slotItem == null){
            val generated = newItem.toItem
            generated.amount = generated.type.stackSize
            getDataInventory().items[slot] = generated
            newItem.amount -= item.type.stackSize
        }
    }

    syncDataToInventory()
    if(newItem.amount > 0){
        val extra = addItem(newItem)
        if(extra.isNotEmpty())
            return extra.values.first()
    } //TODO what happens if can't pick up all items???
    return null
}

fun Inventory.getDataInventory(): Data.Inventory{
    if(holder is HumanEntity) {
        return (holder!! as HumanEntity).data.inventory
    }else{
        throw NotImplementedError("figure out $type!!!, (specifically save and load Data.Inventory from block storage or smthn")
    }
}

fun Inventory.syncSlotFromData(slot: Int){
    val newItem = getDataInventory()[slot]
    setItem(slot, newItem?.itemStack())
}

fun Inventory.syncDataToInventory(){
    sendDebugMessage(DebugType.INVENTORY, viewers,"<blue>Syncing")
    for(slot in 0..35){
        syncSlotFromData(slot)
    }
}

fun Inventory.syncInventoryToData(){
    sendDebugMessage(DebugType.INVENTORY, viewers,"<blue>Syncing")
    val playerData = (holder!! as Player).data
    playerData.inventory.clear()

    for(i in contents.indices){
        val newItem = contents[i]?.toItem ?: continue
        playerData.inventory[i] = newItem
        setItem(i, newItem.itemStack())
    }
}

fun Inventory.syncInventoryToDataNextTick(){
    plugin.nextTick {
        syncInventoryToData()
    }
}

fun Inventory.syncInventoryToDataNextTick(then: () -> Unit){
    plugin.nextTick {
        syncInventoryToData()
        then()
    }
}

fun Inventory.containsItem(item: Item<*>) = this.contains(item.itemStack())