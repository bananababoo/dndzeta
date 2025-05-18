package org.banana_inc.extensions

import com.zorbeytorunoglu.kLib.task.MCDispatcher
import com.zorbeytorunoglu.kLib.task.Scopes
import com.zorbeytorunoglu.kLib.task.suspendFunctionSync
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

/**
 * Spawns an entity in the location.
 * @param entityType EntityType
 */
fun Location.spawnEntity(entityType: EntityType) {
    this.world.spawnEntity(this, entityType)
}

/**
 * Drops the given item stack to the location.
 * @param item ItemStack
 */
fun Location.dropItem(item: ItemStack) {
    world.dropItem(this, item)
}

/**
 * Drops an item stack naturally.
 * @param item ItemStack
 */
fun Location.dropItemNaturally(item: ItemStack) {
    world.dropItemNaturally(this, item)
}

/**
 * Strikes a lightning on the location.
 */
fun Location.strikeLightning() {
    world.strikeLightning(this)
}

/**
 * Sends a lightning strike effect to the location.
 */
fun Location.strikeLightningEffect() {
    world.strikeLightningEffect(this)
}

/**
 * Creates an explosion with the given power in the location.
 * @param power Power of the explosion
 */
fun Location.createExplosion(power: Float) {
    world.createExplosion(this, power)
}

/**
 * Creates an explosion with the given power and an option to enable or disable the fire
 * that will be caused by the explosion.
 * @param power Power float
 * @param fire Should cause fire
 */
fun Location.createExplosion(power: Float, fire: Boolean) {
    world.createExplosion(this, power, fire)
}

/**
 * Creates an explosion with the given power and the option to enable or disable the fire
 * that will be caused by the explosion and option to enable or disable the block break.
 * @param power Power float
 * @param fire Should cause fire boolean
 * @param breakBlocks Should break blocks boolean
 */
fun Location.createExplosion(power: Float, fire: Boolean, breakBlocks: Boolean) {
    world.createExplosion(this.x, this.y, this.z, power, fire, breakBlocks)
}

/**
 * Creates an explosion without causing halt in server with the given power and an option to
 * enable or disable the fire that will be caused by the explosion and block break.
 * @param plugin JavaPlugin instance
 * @param power Power float
 * @param fire Should cause fire boolean
 * @param breakBlocks Should break blocks boolean
 */
suspend fun Location.createExplosionAsync(plugin: JavaPlugin, power: Float, fire: Boolean, breakBlocks: Boolean) {
    Scopes.supervisorScope.launch {

        plugin.suspendFunctionSync {
            world.createExplosion(this@createExplosionAsync.x, this@createExplosionAsync.y, this@createExplosionAsync.z, power, fire, breakBlocks)
        }

    }.join()
}

/**
 * Plays an effect in the location.
 * @param effect Effect
 * @param data Effect data
 */
fun Location.playEffect(effect: Effect, data: Int) {
    world.playEffect(this, effect, data)
}

/**
 * Plays an effect in the location.
 * @param effect Effect
 * @param data Effect data
 * @param radius Radius
 */
fun Location.playEffect(effect: Effect, data: Int, radius: Int) {
    world.playEffect(this, effect, data, radius)
}

/**
 * Plays an effect in the location.
 * @param effect Effect
 * @param data Any
 */
fun <T> Location.playEffect(effect: Effect, data: T) {
    world.playEffect(this, effect, data)
}

/**
 * Plays an effect in the location.
 * @param effect Effect
 * @param data Effect data
 * @param radius Radius
 */
fun <T> Location.playEffect(effect: Effect, data: T, radius: Int) {
    world.playEffect(this, effect, data, radius)
}

/**
 * Spawns entities without causing halt.
 * @param mcPlugin JavaPlugin instance
 * @param entityType EntityType
 */
suspend fun Location.spawnEntityAsync(mcPlugin: JavaPlugin, entityType: EntityType) {

    Scopes.supervisorScope.launch(MCDispatcher(mcPlugin, async = false)) {
        mcPlugin.suspendFunctionSync { world.spawnEntity(this@spawnEntityAsync, entityType) }
    }.join()

}

/**
 * Gives the location as a legible String.
 * @return String
 */
fun Location.toLegibleString(): String =
    "${this.world.name};${this.x};${this.y};${this.z};${this.yaw};${this.pitch}"

/**
 * Converts a legible Location String to Location.
 * @return Location
 */
fun Location.fromLegibleString(string: String): Location {

    val args: List<String> = string.split(";")

    return Location(Bukkit.getWorld(args[0]),args[1].toDouble(),args[2].toDouble(),args[3].toDouble(),
        args[4].toFloat(), args[5].toFloat())

}

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