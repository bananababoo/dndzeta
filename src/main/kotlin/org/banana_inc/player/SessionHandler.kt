package org.banana_inc.player

import org.banana_inc.EventManager
import org.banana_inc.data.Data
import org.banana_inc.data.DatabaseActions
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

@InitOnStartup
object SessionHandler {
    init {
        EventManager.addListener<AsyncPlayerPreLoginEvent> {
            DatabaseActions.load<Data.Player>(it.uniqueId) ?: DatabaseActions.store(Data.Player(it.uniqueId))
        }
    }
}