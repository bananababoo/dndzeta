package org.banana_inc.mechanics.graphics.geometry

import com.github.retrooper.packetevents.protocol.world.states.type.StateType
import com.github.retrooper.packetevents.util.Vector3f
import org.banana_inc.visuals.geometry.Box
import org.bukkit.Location
import org.bukkit.entity.Player

class Line(player: Player, origin: Location, block: StateType, width: Float, height: Float, destination: Location):

    Box(player, origin, block,
        Vector3f(0f,0f,0f),
        getScale(origin, destination, height, width)
    ){

    fun update(origin: Location, destination: Location, width: Float, height: Float) {
        super.scale = getScale(origin, destination, height, width)
        super.redisplay(origin)
    }

    companion object{
        fun getScale(origin: Location, destination: Location, height: Float, width: Float): Vector3f {
            val length = destination.subtract(origin).length().toFloat()
            return Vector3f(width, height, length)
        }
    }

}