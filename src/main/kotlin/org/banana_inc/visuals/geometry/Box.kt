package org.banana_inc.visuals.geometry

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState
import com.github.retrooper.packetevents.protocol.world.states.type.StateType
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.util.Vector3f
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

open class Box(player: Player, origin: Location, var block: StateType, translation: Vector3f=Vector3f(0f,0f,0f), scale: Vector3f=Vector3f(0.0625f,0.0625f,0.0625f))
    :Object3D(player, origin, translation, scale) {

    init {
        spawn()
    }

    override fun makeSpawnPacket(): WrapperPlayServerSpawnEntity {
        return WrapperPlayServerSpawnEntity(
            id,
            UUID.randomUUID(),
            EntityTypes.BLOCK_DISPLAY,
            com.github.retrooper.packetevents.protocol.world.Location(
                Vector3d(origin.x,origin.y,origin.z), origin.yaw, origin.pitch),
            0f,
            0,
            null
        )
    }

    override fun makeUpdateEntityDataPacket(): WrapperPlayServerEntityMetadata {
        return WrapperPlayServerEntityMetadata(id,
            listOf(
                EntityData(9, EntityDataTypes.INT, 2), //transformation interpolation
                EntityData(10, EntityDataTypes.INT, 2), //Pos/Rotation interpolation
                EntityData(11, EntityDataTypes.VECTOR3F, translation), //translation
                EntityData(12, EntityDataTypes.VECTOR3F, scale), //scale
                EntityData(23, EntityDataTypes.BLOCK_STATE, WrappedBlockState.getDefaultState(block).globalId)
            )
        )
    }

}