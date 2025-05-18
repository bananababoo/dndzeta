package org.banana_inc.debug

import org.banana_inc.debug.Debug.token
import org.banana_inc.extensions.sendMessage
import org.banana_inc.util.storage.StorageToken
import org.banana_inc.util.storage.tempStorage
import org.bukkit.Bukkit
import org.bukkit.entity.HumanEntity
import java.util.*

object Debug{
    val token = StorageToken<EnumMap<DebugType,Boolean>>("debug")
}

fun sendDebugMessage(mode: DebugType, message: String){
    for(p in Bukkit.getOnlinePlayers().filter { it.isOp }){
        if(p.tempStorage[token]!!.containsKey(mode)){
            sendMessage(p, "<gray>[Debug-$mode]: <white>$message")
        }
    }
}

fun sendDebugMessage(mode: DebugType, player: HumanEntity, message: String){
    if (player.tempStorage[token] != null && player.tempStorage[token]!!.containsKey(mode)) {
        sendMessage(player, "<gray>[Debug-$mode]: <white>$message")
    }
}

fun sendDebugMessage(mode: DebugType, players: List<HumanEntity>, message: String){
    for(player in players) {
        if (player.tempStorage[token] != null && player.tempStorage[token]!!.containsKey(mode)) {
            sendMessage(player, "<gray>[Debug-$mode]: <white>$message")
        }
    }
}