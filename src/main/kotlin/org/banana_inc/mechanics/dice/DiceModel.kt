package org.banana_inc.mechanics.dice

import com.zorbeytorunoglu.kLib.task.Scopes
import com.zorbeytorunoglu.kLib.task.sync
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.banana_inc.visuals.model.ActiveModel
import org.banana_inc.plugin
import org.bukkit.Location
import kotlin.time.Duration.Companion.seconds

class DiceModel(sides: Dice.Sides, side: Int, spawnLocation: Location, asyncFinishedRolling: suspend () -> Unit):
    ActiveModel(sides.diceNotation, spawnLocation, "roll$side") {

    init {
        Scopes.supervisorScope.launch {
            delay(5.seconds)
            asyncFinishedRolling()
            plugin.sync {
                remove()
            }
        }
    }

}