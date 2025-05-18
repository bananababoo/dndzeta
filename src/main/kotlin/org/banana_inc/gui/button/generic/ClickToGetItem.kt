package org.banana_inc.gui.button.generic

import org.banana_inc.extensions.addItem
import org.banana_inc.item.Item
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import xyz.xenondevs.invui.Click
import xyz.xenondevs.invui.item.AbstractPagedGuiBoundItem
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper

open class ClickToGetItem(val item: Item<*>): AbstractPagedGuiBoundItem() {

    override fun getItemProvider(p0: Player): ItemProvider {
        return ItemWrapper(item.itemStack())
    }

    override fun handleClick(p0: ClickType, player: Player, p2: Click) {
        player.closeInventory()
        player.inventory.addItem(item)
    }
}