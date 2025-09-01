package org.banana_inc.mechanics.battle.visual

import com.github.retrooper.packetevents.protocol.world.states.type.StateType
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes
import com.github.retrooper.packetevents.util.Vector3f
import org.banana_inc.extensions.relativeOffset
import org.banana_inc.visuals.geometry.Box
import org.bukkit.FluidCollisionMode
import org.bukkit.entity.Player

object SelectionBox {

    fun makeBox(player: Player, block: StateType): Box {
        val start = player.eyeLocation.relativeOffset(0.05,-0.05,1.0)
        val rayTraceResult = player.location.world.rayTraceBlocks(start, player.eyeLocation.direction, 20.0, FluidCollisionMode.ALWAYS, true)
        val end = if(rayTraceResult?.hitPosition != null) rayTraceResult.hitPosition.toLocation(player.world) else
            player.eyeLocation.add(player.eyeLocation.direction.multiply(20))
        val block =
            if(end.subtract(start).length() > 10){
                StateTypes.RED_STAINED_GLASS
            }else{
                StateTypes.LIME_STAINED_GLASS
            }

        return Box(player, start, block, Vector3f(0f,0f,0f), Vector3f(0.1f,0.1f,0.1f))
    }

    fun updateBox(player: Player, box: Box){
        val start = player.eyeLocation.relativeOffset(0.05,-0.05,1.0)
        val rayTraceResult = player.location.world.rayTraceBlocks(start, player.eyeLocation.direction, 20.0, FluidCollisionMode.ALWAYS, true)
        val end = if(rayTraceResult?.hitPosition != null) rayTraceResult.hitPosition.toLocation(player.world) else
            player.eyeLocation.add(player.eyeLocation.direction.multiply(20))
        if(end.subtract(start).length() > 10){
            box.block = StateTypes.RED_STAINED_GLASS
        }else{
            box.block = StateTypes.LIME_STAINED_GLASS
        }
        box.scale = Vector3f(0.1f, 0.1f, 0.1f)

        box.redisplay(start)
    }
}