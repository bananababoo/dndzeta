package org.banana_inc.item.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.banana_inc.extensions.itemInMainHand
import org.banana_inc.extensions.syncSlotFromData
import org.bukkit.entity.Player

@CommandAlias("item_clone|_ic")
object CloneCommand: BaseCommand() {

    @Default
    fun clone(player: Player) {
        val item = player.itemInMainHand ?: return
        val newAmount = item.amount * 2
        if(newAmount < 0 ) return // int overflow to negative
        item.amount *= 2
        player.inventory.syncSlotFromData(player.inventory.heldItemSlot)
    }
}