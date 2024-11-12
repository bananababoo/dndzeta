package org.banana_inc.extensions

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * Calculates a new location by applying an offset relative to the current direction of the location.
 * This function allows for movement along the x, y, and z axes, where:
 *
 * @param x the offset in the right direction (positive values move right, negative values move left)
 * @param y the offset in the upward direction (positive values move up, negative values move down)
 * @param z the forward offset in the direction the location is facing
 * @return this location, modified to the relative offset
 */
fun Location.relativeOffset(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0): Location {

    val worldUp = Vector(0.0, 1.0, 0.0)
    val direction = this.direction

    val right = direction.clone().crossProduct(worldUp).normalize()
    val upward = right.clone().crossProduct(direction).normalize()

    return this.add(direction.multiply(z)).add(upward.multiply(y)).add(right.multiply(x))
}

fun Location.asyncNearbyPlayers(radius: Long): List<Player> {
    val radiusSquared = radius * radius
    val list = mutableListOf<Player>()
    for (player in Bukkit.getOnlinePlayers()) {
        if (distanceSquared(player.location) < radiusSquared) {
            list.add(player)
        }
    }
    return list
}