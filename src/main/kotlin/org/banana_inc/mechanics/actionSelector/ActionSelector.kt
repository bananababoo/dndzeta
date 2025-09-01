package org.banana_inc.mechanics.actionSelector

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.github.retrooper.packetevents.util.Vector3f
import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.launch
import org.banana_inc.logger
import org.banana_inc.onPlayer
import org.banana_inc.oncePlayer
import org.banana_inc.visuals.geometry.Stamp
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import org.joml.Vector3d
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object ActionSelector {

    fun start(player: Player, vararg items: ItemType, callback: (itemIndex: Int) -> Unit) {
        if(items.size == 1) return

        val forward = player.eyeLocation.direction.toVector3d()
        val worldUp = Vector3d(0.0, 1.0, 0.0)
        val right = Vector3d(forward).cross(worldUp).normalize()
        val up = Vector3d(right).cross(Vector3d(forward)).normalize()

        val stamps = mutableListOf<Stamp>()
        for(i in 0..< items.size) {
            val theta = ((2*PI / items.size) * i).toFloat()

            val result = projectOntoSphere(player, theta).mul(2.0)
            val location = player.eyeLocation.add(Vector.fromJOML(result))
            location.setDirection(Vector.fromJOML(result))

            val item = ItemStack.builder().type(items[i]).build()
            val scale = 1.1f - ((items.size) * 0.07f)
            stamps.add(Stamp(player, location, item, scale= Vector3f(scale, scale, scale)).apply{ spawn() })
        }

        var previousSection = -1
        var currentSection = -1

        val onPlayerLook = onPlayer<PlayerMoveEvent>(player) {
            Scopes.defaultScope.launch {
                val dir = to.direction.toVector3d()

                val intersection = to.direction.toVector3d().div(forward.dot(dir))
                val relativePoint = intersection.sub(dir)
                val planeX = relativePoint.dot(right)
                val planeY = relativePoint.dot(up)

                var radians = atan2(planeY, planeX)
                val boundingOffset = PI / items.size
                if (radians < -boundingOffset) radians += 2 * PI
                radians += boundingOffset

                currentSection = (radians / ((2 * PI) / items.size)).toInt()

                if (previousSection == currentSection) return@launch

                stamps[currentSection].setGlowing(true)
                if (previousSection != -1) {
                    stamps[previousSection].setGlowing(false)
                }
                previousSection = currentSection
            }
        }

        oncePlayer<PlayerInteractEvent>(player){
            player.sendMessage("action, $action, $this")
            callback(currentSection)
            onPlayerLook.remove()
            for (stamp in stamps) {
                stamp.remove()
            }
        }
    }

    //https://claude.ai/chat/363cad2e-68f7-4b1d-882e-db0c397e5d5a

    private fun projectOntoSphere(player: Player, theta: Float): Vector3d {
        val forward = player.eyeLocation.direction.toVector3d()
        forward.y = 0.0
        forward.normalize()

        logger.info("${player.eyeLocation} eye loc, eye loc dir ${player.eyeLocation.direction}, forward: $forward")
        val worldUp = Vector3d(0.0, 1.0, 0.0)

        val right = Vector3d(forward).cross(worldUp).normalize()
        val up = Vector3d(right).cross(Vector3d(forward)).normalize()

        val coneAngle = (PI / 8).toFloat()

        val x = cos(theta) * sin(coneAngle)
        val y = sin(theta) * sin(coneAngle)
        val z = cos(coneAngle)
        logger.info("x, $x, y $y, z $z, theta $theta, coneAngle $coneAngle, right $right, up $up, forward $forward")

        return Vector3d()
            .add(Vector3d(right).mul(x.toDouble()))
            .add(Vector3d(up).mul(y.toDouble()))
            .add(Vector3d(forward).mul(z.toDouble()))
            .normalize().rotateAxis(Math.toRadians(-player.eyeLocation.pitch.toDouble()),right.x, right.y, right.z)
    }


}