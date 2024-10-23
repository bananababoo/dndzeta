package org.banana_inc.extensions

import org.bukkit.Sound
import org.bukkit.World

/**
 * Removes all the entities in the world's list.
 */
fun World.removeEntities() = this.entities.forEach { it.remove() }


/**
 * Plays sound for player in the world.
 * @param sound Sound
 * @param volume Volume Float
 * @param pitch Pitch Float
 */
fun World.playSound(sound: Sound, volume: Float, pitch: Float) {
    this.players.forEach {
        it.playSound(sound, volume, pitch)
    }
}