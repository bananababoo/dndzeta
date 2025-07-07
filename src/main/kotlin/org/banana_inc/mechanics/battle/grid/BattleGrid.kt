package org.banana_inc.mechanics.battle.grid

import org.banana_inc.util.storage.StorageToken
import org.banana_inc.util.storage.tempStorage
import org.bukkit.Location
import org.bukkit.entity.Player

class BattleGrid(val player: Player) {

    companion object {
        val storageToken: StorageToken<BattleGrid> = StorageToken("battle_grid")
    }

    val initialXOffset = player.location.x
    val initialZOffset = player.location.z

    val grid: HashMap<GridPosition, GridEntity> = HashMap()

    init {
        initGrid(32)
    }

    fun update(){

    }

    fun remove(){
        grid.values.forEach {
            it.entity.remove()
        }
    }

    fun initGrid(radius: Int){
        player.tempStorage[storageToken] = this

        for(x in -radius..radius){
            for(z in -radius..radius){
                val xEven: Boolean = x % 2 == 0
                val zEven: Boolean = z % 2 == 0
                if(x* x + z * z > radius * radius || xEven && zEven) continue // if outside circle or in hole skip
                else if(!xEven && !zEven) grid[GridPosition(x,z)] = GridEntity(GridEntity.Type.CENTER,toWorldSpace(x,z))
                else if(xEven) grid[GridPosition(x,z)] = GridEntity(GridEntity.Type.Z_EDGE,toWorldSpace(x,z))
                else grid[GridPosition(x,z)] = GridEntity(GridEntity.Type.X_EDGE,toWorldSpace(x,z))
            }
        }
    }

    fun toWorldSpace(x: Int, z: Int): Location {
        return Location(player.location.world,
            initialXOffset + x,
            player.location.y,
            initialZOffset + z)
    }

    fun toWorldSpace(gridPosition: GridPosition): Location {
        return toWorldSpace(gridPosition.x,gridPosition.z)
    }

    //input -1, 0 or 1 for all
    fun expandGrid(x: Int, z: Int) {

    }

}