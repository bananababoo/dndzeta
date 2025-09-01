package org.banana_inc.visuals.model

import com.zorbeytorunoglu.kLib.task.nextTick
import org.banana_inc.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemDisplay

open class ActiveModel(private val modelName: String, spawnLocation: Location, startingAnimation: String? = null) {

    private var entity: Entity? = null

    companion object {
        const val STATIC_MODEL_TAG = "dndzeta.static"
    }

    init {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
        "execute " +
                    "positioned ${spawnLocation.x} ${spawnLocation.y} ${spawnLocation.z} " +
                    "rotated ${spawnLocation.yaw} ${spawnLocation.pitch} " +
                    "run function animated_java:$modelName/summon {args:{}}"
        )

        plugin.nextTick {
            val filteredItemDisplays = spawnLocation.chunk.entities.filterIsInstance<ItemDisplay>().filter {
                it.scoreboardTags.contains("aj.$modelName.root") && it.ticksLived == 0 && spawnLocation.distanceSquared(it.location) < 1.0
            }
            if(filteredItemDisplays.size == 1){
                entity = filteredItemDisplays[0]
            } else {
                error("Model $modelName failed to spawn or be tracked at $spawnLocation")
            }
            if(startingAnimation != null){
                animate(startingAnimation)
            }
        }
    }

    fun animate(animation: String){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
        "execute as ${entity!!.uniqueId} " +
                    "run function animated_java:$modelName/animations/$animation/play"
        )
    }

    fun remove(){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
        "execute as ${entity!!.uniqueId} " +
                    "run function animated_java:$modelName/remove/this"
        )
    }
}