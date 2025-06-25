package org.banana_inc.extensions

import org.bukkit.block.Block
import org.bukkit.block.BlockFace

/**
 * Finds the next solid block in a direction, starting from a block.
 * null if no solid
 */
fun Block.findNextSolidBlock(direction: BlockFace, getLastEmptyBeforeWall: Boolean = false): Block? {
    var checkingBlock = this
    var cameFromSolids = false

    while(true){
        val relative = checkingBlock.getRelative(direction)

        if(direction == BlockFace.UP && checkingBlock.lightFromSky.toInt() == 15) return null

        if((checkingBlock.isSolid && (!relative.isSolid || relative.isEmpty))){
            checkingBlock = checkingBlock.getRelative(direction)
            cameFromSolids = true
            break
        }
        if((relative.isSolid && (!checkingBlock.isSolid || checkingBlock.isEmpty))){
            checkingBlock = checkingBlock.getRelative(direction)
            break
        }
        checkingBlock = checkingBlock.getRelative(direction)
        if(checkingBlock.y < -64 || checkingBlock.y > 300) error("Out of range ${checkingBlock.location.toLegibleString()}")
    }

    if(getLastEmptyBeforeWall && !cameFromSolids){
        return checkingBlock.getRelative(direction.oppositeFace) // go back one
    }
    return checkingBlock
}