package org.banana_inc.mechanics.dice

import org.banana_inc.extensions.findNextSolidBlock
import org.banana_inc.extensions.relativeOffset
import org.banana_inc.logger
import org.banana_inc.util.dnd.Dice
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.LivingEntity

class DiceRoll(val entity: LivingEntity, sides: Dice.Sides, val asyncRollResult: suspend (Int) -> Unit) {

    private val rollLocation = getDiceRollLocation()
    private val result = (Math.random() * sides.sides + 1).toInt()
    val model = DiceModel(sides, result, rollLocation) { asyncRollResult(result) }

    private fun getProjectedLocation(iteration: Int): Location{
        val eyeLocation = entity.eyeLocation
        val offsetX = when(iteration){
            1,4 -> 0.0
            2 -> -1.5
            3 -> 1.5
            else -> error("while loop kept going when finding dice roll model location")
        }
        val offsetZ = when(iteration){
            1,2,3 -> 3.0
            4 -> -3.0
            else -> error("while loop kept going when finding dice roll model location")
        }

        return eyeLocation.relativeOffset(offsetX, 0.0, offsetZ)
    }

    private fun getScanDirection(projected: Location): BlockFace{
        val blockFace: BlockFace
        if(projected.y > entity.location.y){
            blockFace = BlockFace.DOWN
        } else {
            val projectedBlock = projected.block
            blockFace = if(!projectedBlock.isSolid || projectedBlock.isEmpty){
                BlockFace.DOWN
            }else {
                BlockFace.UP
            }
        }
        return blockFace
    }

    private fun getDiceRollLocation(): Location{
        for(i in 1..4) {
            logger.info("i: $i")
            val projected = getProjectedLocation(i)
            val scanDirection = getScanDirection(projected)
            val ground = projected.block.findNextSolidBlock(scanDirection, true)

            if(ground == null) {
                if(i==4){
                    return projected.toHighestLocation().add(0.0,1.0,0.0)
                }
                continue
            }

            val heightDiff = projected.y - ground.y

            if (heightDiff > -15 && heightDiff < 4) {
                projected.y -= heightDiff
                projected.pitch = 0f
                return projected
            }

            if(i==4){
                return projected.toHighestLocation().add(0.0,1.0,0.0)
            }

        }
        return entity.location
    }

}