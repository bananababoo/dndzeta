package org.banana_inc.creator

import org.bukkit.Material
import org.bukkit.entity.Player
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.SimpleItem
import xyz.xenondevs.invui.window.AnvilWindow

class ItemCreator(player: Player) {

    val border = SimpleItem(ItemBuilder(Material.BLACK_STAINED_GLASS_PANE))

    private val gui = Gui.normal()
        .setStructure(
            "# # # # # # # # #",
            "# . . . . . . . #",
            "# . . . . . . . #",
            "# . . . . . . . #",
            "# . . . . . . . #",
            "# < # + F S # > #" // left add filter search
        )

    private val window = AnvilWindow.single()
        .setViewer(player)
        .setTitle("InvUI")
        .setGui(gui)
        .build()
    init {
        window.open()
    }

}