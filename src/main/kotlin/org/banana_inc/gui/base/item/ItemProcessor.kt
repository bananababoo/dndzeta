package org.banana_inc.gui.base.item

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

interface ItemProcessor{
    fun process(builder: ItemStack, viewer: Player): ItemStack
    val priority: Int get() = 0
    fun handleClick(click: ClickType, player: Player)
}