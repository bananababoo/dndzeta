package org.banana_inc.model

import org.banana_inc.EventManager
import org.banana_inc.ServerSession
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.world.ChunkLoadEvent

@InitOnStartup
object RemoveInvalidModels {

    init {
        onChunkLoad()
    }

    private fun onChunkLoad(){
        EventManager.addListener<ChunkLoadEvent> {
            val displays = chunk.entities.filterIsInstance<ItemDisplay>().filter {
                 !it.scoreboardTags.contains(ActiveModel.STATIC_MODEL_TAG)
            }
            for (display in displays) {
                if(!display.scoreboardTags.contains(ServerSession.sessionID.toString())){
                    display.remove()
                }
            }
        }
    }

}