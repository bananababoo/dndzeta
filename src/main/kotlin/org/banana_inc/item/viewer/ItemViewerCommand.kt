package org.banana_inc.item.viewer

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.bukkit.entity.Player

@CommandAlias("item_viewer|items|iv")
object ItemViewerCommand: BaseCommand() {

    @Default
    fun viewItems(player: Player){
        ItemViewer(player)
    }

}