package org.banana_inc.player

import com.zorbeytorunoglu.kLib.task.nextTick
import org.banana_inc.data.Data
import org.banana_inc.data.PlayerData
import org.banana_inc.data.database.DatabaseActions
import org.banana_inc.extensions.data
import org.banana_inc.on
import org.banana_inc.plugin
import org.banana_inc.util.initialization.InitOnStartup
import org.banana_inc.util.storage.tempStorage
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("unused")
@InitOnStartup
object SessionHandler {
    init {
        on<AsyncPlayerPreLoginEvent> {
            //---Database---
            DatabaseActions.load<PlayerData>(uniqueId) ?: DatabaseActions.store(PlayerData(uniqueId))
        }

        on<PlayerQuitEvent> {
            plugin.nextTick { // other evets such as inventory close event might fire after this one, we wait for those
                player.tempStorage.wipe()
                //---Database---
                DatabaseActions.update(player.data)

                //---Cached Data---
                Data.unload<PlayerData>(player.uniqueId)
            }
        }

        on<PlayerJoinEvent>{
            player.inventory.clear()
            for (item in player.data.inventory) {
                player.inventory.setItem(item.key, item.value.itemStack())
            }
        }
    }
}