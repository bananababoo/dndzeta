package org.banana_inc.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import xyz.xenondevs.invui.item.AbstractPagedGuiBoundItem
import xyz.xenondevs.invui.item.Click
import xyz.xenondevs.invui.item.ItemBuilder
import xyz.xenondevs.invui.item.ItemProvider

class ChangePageItem : AbstractPagedGuiBoundItem() {

    override fun getItemProvider(p0: Player): ItemProvider {
        return ItemBuilder(Material.TIPPED_ARROW)
            .setName("Current page: " + (gui.page + 1)) // + 1 because we don't want to have "Current page: 0"
            .addLoreLines("Left-click to go forward", "Right-click to go back")
    }

    override fun handleClick(clickType: ClickType, p1: Player, p2: Click) {
        if (clickType == ClickType.LEFT) {
            gui.goForward() // go one page forward on left-click
        } else if (clickType == ClickType.RIGHT) {
            gui.goBack() // go one page back on right-click
        }
    }

}