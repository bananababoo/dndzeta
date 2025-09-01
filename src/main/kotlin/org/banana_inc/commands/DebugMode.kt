package org.banana_inc.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import org.banana_inc.util.debug.Debug
import org.banana_inc.util.debug.DebugType
import org.banana_inc.extensions.sendMessage
import org.banana_inc.util.storage.tempStorage
import org.bukkit.entity.Player
import java.util.*

@CommandAlias("debugmode")
object DebugMode: BaseCommand() {

    @Subcommand("view")
    fun view(player: Player){
        sendMessage(player,
            "<gray>Current Debugs Enabled:<white> ${player.tempStorage[Debug.token] ?: "None"}"
        )
    }

    @Subcommand("toggle")
    fun toggle(player: Player, option: DebugType){
        player.tempStorage.putIfAbsent(Debug.token, EnumMap(DebugType::class.java))
        player.tempStorage[Debug.token]!!.compute(option){ _, value ->
            if(value == null) return@compute true
            val result = !value
            sendMessage(player, "Updated Debug State: $option $result")
            result
        }
    }

}