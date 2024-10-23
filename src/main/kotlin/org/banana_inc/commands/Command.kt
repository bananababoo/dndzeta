package org.banana_inc.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.banana_inc.plugin
import org.bukkit.entity.Player

@CommandAlias("test")
class Command: BaseCommand() {

    @Default
    fun testCommand(player: Player){
        player.sendMessage("hello")
        player.sendMessage(plugin.toString())
    }

}