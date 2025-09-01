package org.banana_inc.mechanics.classes.classes

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.banana_inc.data.attributes.AttributeModifier
import org.banana_inc.data.attributes.PlayerAttributes
import org.banana_inc.item.attributes.weapon.WeaponProficiency
import org.banana_inc.mechanics.classes.Ability
import org.banana_inc.mechanics.classes.Skill
import org.banana_inc.mechanics.classes.features.ClassFeature
import org.banana_inc.mechanics.dice.Dice

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
abstract class Class(
    val unlockedFeatures: Set<ClassFeature>,
): AttributeModifier {
    var level: Int = 1

    protected abstract val details: Details

    override fun modifyAttributes(player: PlayerAttributes) {
        player.maxHP.putIfAbsent("BASE_CLASS_HP", details.hpDie.getSides())
        player.maxHP.putIfAbsent("BASE_CLASS_HP_ABILITY_MOD",player.abilityScores.getMod(Ability.CONSTITUTION))
        player.maxHP["TOTAL_${this::class.simpleName}_PER_ADDITIONAL_LEVEL_HP"] = (details.hpDie.getSides() / 2) * level + player.abilityScores.getMod(
            Ability.CONSTITUTION)
    }

    protected abstract class FeatureDetails{
        abstract fun getClassFeatures(): Map<Int, Set<ClassFeature>>
        fun getClassFeature(level: Int): Set<ClassFeature> {
            return getClassFeatures()[level]?: setOf()
        }
    }

    data class Details(
        val hpDie: Dice,
        val savingThrowProficiencies: Set<Ability>,
        val skillProficiencies: Set<Skill>,
        val weaponProficiencies: Set<WeaponProficiency>){
    }

    override fun toString(): String {
        return "${this::class.simpleName}[level=$level, unlockedFeatures=$unlockedFeatures"
    }

}