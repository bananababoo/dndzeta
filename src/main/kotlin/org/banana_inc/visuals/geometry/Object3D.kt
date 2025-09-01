package org.banana_inc.visuals.geometry

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.util.Vector3f
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class Object3D(val player: Player, protected var origin: Location, var translation: Vector3f=Vector3f(0f,0f,0f), var scale: Vector3f=Vector3f(0.0625f,0.0625f,0.0625f)) {

    @Suppress("DEPRECATION")
    val id = Bukkit.getUnsafe().nextEntityId()
    private var glowing = false

    protected abstract fun makeSpawnPacket(): WrapperPlayServerSpawnEntity
    protected abstract fun makeUpdateEntityDataPacket(): WrapperPlayServerEntityMetadata

    fun spawn() {
        player.sendMessage("sending packets")
        PacketEvents.getAPI().playerManager.sendPacket(player,makeSpawnPacket())
        PacketEvents.getAPI().playerManager.sendPacket(player,makeUpdateEntityDataPacket())
    }


    fun redisplay(newLocation: Location?=null) {
        PacketEvents.getAPI().playerManager.sendPacket(player, makeUpdateEntityDataPacket())
        if(newLocation != null){
            PacketEvents.getAPI().playerManager.sendPacket(player, makeMovePacket(newLocation))
        }
    }

    fun remove() {
        PacketEvents.getAPI().playerManager.sendPacket(player, WrapperPlayServerDestroyEntities(id))
    }

    fun setGlowing(glow: Boolean) {
        glowing = glow
        PacketEvents.getAPI().playerManager.sendPacket(player,  WrapperPlayServerEntityMetadata(id,
            listOf(
                EntityData(0,EntityDataTypes.BYTE,if(glow) 0x40 else 0x0)
            )
        ))
    }

    protected fun makeMovePacket(newLocation: Location): WrapperPlayServerEntityRelativeMoveAndRotation {
        val delta = newLocation.clone().subtract(origin)
        origin = newLocation
        return WrapperPlayServerEntityRelativeMoveAndRotation(id, delta.x, delta.y, delta.z, newLocation.yaw, newLocation.pitch, false)
    }

    companion object {
        fun removeAll(player: Player, vararg gridPieces: Box){
            val gridIds = gridPieces.map { it.id }.toIntArray()
            val removeEntityPacket = WrapperPlayServerDestroyEntities(*gridIds)
            PacketEvents.getAPI().playerManager.sendPacket(player, removeEntityPacket)
        }
    }

}