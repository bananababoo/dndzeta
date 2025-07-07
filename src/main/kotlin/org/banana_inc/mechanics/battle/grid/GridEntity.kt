package org.banana_inc.mechanics.battle.grid

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.BlockDisplay

class GridEntity(val type: Type, location: Location) {

    val entity = location.world.spawn(location, BlockDisplay::class.java){
        it.block = when(type){
            Type.CENTER -> Material.GOLD_BLOCK.createBlockData()
            Type.X_EDGE -> Material.REDSTONE_BLOCK.createBlockData()
            Type.Z_EDGE -> Material.EMERALD_BLOCK.createBlockData()
        }
    }

    enum class Type{
        CENTER,
        X_EDGE, //+east -west
        Z_EDGE, //+south -north
    }

}