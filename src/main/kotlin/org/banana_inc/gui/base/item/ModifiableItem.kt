package org.banana_inc.gui.base.item

import org.banana_inc.logger
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.invui.Click
import xyz.xenondevs.invui.item.AbstractBoundItem
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import java.util.*

abstract class ModifiableItem(protected var item: ItemStack): AbstractBoundItem() {

    private val processors = TreeSet(compareBy<ItemProcessor>{ it.priority }.reversed())

    final override fun getItemProvider(viewer: Player): ItemProvider {
        logger.info("getting provider")
        processors.forEach { item = it.process(item,viewer) }
        return ItemWrapper(item)
    }

    fun addProcessor(processor: ItemProcessor) {
        processors.add(processor)
    }

    final override fun handleClick(clickType: ClickType, player: Player, click: Click) {
        logger.info("handleing click")
        processors.forEach { it.handleClick(clickType,player) }
        onClick(clickType,player,click)
        notifyWindows()
    }

    abstract fun onClick(clickType: ClickType, player: Player, click: Click)

}