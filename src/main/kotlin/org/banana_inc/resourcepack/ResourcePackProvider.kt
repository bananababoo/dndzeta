package org.banana_inc.resourcepack

import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.launch
import org.banana_inc.EventManager
import org.banana_inc.config.ServerConfig
import org.banana_inc.extensions.data
import org.banana_inc.logger
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

@InitOnStartup
object ResourcePackProvider {

    init {
        EventManager.addListener<PlayerJoinEvent> { //TODO change
            applyAllResourcePacks(player)
        }
    }

    fun reloadResourcePackAsync() {
        Scopes.ioScope.launch {
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                applyAllResourcePacks(onlinePlayer)
            }
        }
    }

    private fun applyAllResourcePacks(player: Player) {
        for(resourcePack in player.data.settings.resourcePackOptions){
            applyResourcePack(player, resourcePack)
        }
    }

    fun applyResourcePack(player: Player, type: ServerConfig.ResourcePackConfig.Data) {
        logger.info("applying resource pack: $type")
        player.addResourcePack(type.id,
            type.url,
            type.hash.toByteArray(),
            "AAAAAAAAAAAA" ,
            true)
    }

    fun removeResourcePack(player: Player, type: ServerConfig.ResourcePackConfig.Data){
        logger.info("applying resource pack: $type")
        player.removeResourcePack(type.id)
    }

}