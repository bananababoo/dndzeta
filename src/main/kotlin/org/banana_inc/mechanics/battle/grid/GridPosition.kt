package org.banana_inc.mechanics.battle.grid

data class GridPosition(val x: Int, val z: Int)  {

    init {
        if(x % 2 == 0 && z % 2 == 0){
            error("Illegal grid position $x $z")
        }
    }

}