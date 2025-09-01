package org.banana_inc.data.attributes

import org.banana_inc.item.attributes.weapon.WeaponProficiency
import org.banana_inc.mechanics.classes.Ability
import org.banana_inc.mechanics.classes.Skill
import org.banana_inc.mechanics.classes.action.Action


data class PlayerAttributes(
    val maxHP: Attribute<Int> = Attribute(),

    val abilityScores: AbilityScores = AbilityScores(),

    val savingThrowProficiencies: Attribute<Set<Ability>> = Attribute(),
    val skillProficiencies: Attribute<Set<Skill>> = Attribute(),
    val weaponProficiencies: Attribute<Set<WeaponProficiency>> = Attribute(),

    val actions: Attribute<Action> = Attribute(),
    val bonusActions: Attribute<Action> = Attribute(),

    )

data class AbilityScores(val abilityScores: MapAttribute<Ability,Int> = MapAttribute()){
    fun getMod(ability: Ability): Int = ( (abilityScores[ability] ?: 10 ) - 10)/2
}
