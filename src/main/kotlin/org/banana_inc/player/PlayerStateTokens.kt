package org.banana_inc.player

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.banana_inc.util.storage.StorageToken

object PlayerStateTokens {
    object Battle {
        val LISTEN_FOR_RAY = StorageToken<ScheduledTask>("in_battle")
    }
}