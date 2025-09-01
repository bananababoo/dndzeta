package org.banana_inc.mechanics.classes.features.barbarian

import org.banana_inc.data.attributes.PlayerAttributes
import org.banana_inc.item.items.Armor
import org.banana_inc.mechanics.classes.action.Action
import org.banana_inc.mechanics.classes.features.ClassFeature

data class Rage(
    val rages: Int = rageAmountPerLevel[1]!!,
    var raging: Boolean = false
): ClassFeature() {

    override fun modifyAttributes(player: PlayerAttributes) {
        player.actions["RAGE_BONUS_ACTION"] = Action({ player ->
            raging = true
        }, { player ->
            player.inventory.equipment.armor !is Armor.Heavy
        })
    }

    companion object{
        val rageAmountPerLevel = mapOf<Int, Int>(
            1 to 2,
            2 to 2,
            3 to 3,
            4 to 3,
            5 to 3,
            6 to 3,
            7 to 4,
            8 to 4,
            9 to 4,
            10 to 4,
            11 to 4,
            12 to 5,
            13 to 5,
            14 to 5,
            15 to 5,
            16 to 5,
            17 to 6,
            18 to 6,
            19 to 6,
            20 to 6
        )
    }

}