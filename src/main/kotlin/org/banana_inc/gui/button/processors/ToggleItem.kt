package org.banana_inc.gui.button.processors

import org.banana_inc.extensions.changeMeta
import org.banana_inc.gui.base.item.ItemProcessor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class ToggleItem(var glow: Boolean, var enabled: Boolean = false): ItemProcessor {
    override fun handleClick(click: ClickType, player: Player){
        enabled = !enabled
    }

    override fun process(builder: ItemStack, viewer: Player): ItemStack{
        return builder.changeMeta {
            setEnchantmentGlintOverride(enabled)
        }
    }
}