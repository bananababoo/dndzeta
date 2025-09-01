package org.banana_inc.mechanics.classes.classes.barbarian

import org.banana_inc.item.attributes.weapon.WeaponProficiency
import org.banana_inc.mechanics.classes.Ability
import org.banana_inc.mechanics.classes.Skill
import org.banana_inc.mechanics.classes.classes.Class
import org.banana_inc.mechanics.classes.features.ClassFeature
import org.banana_inc.mechanics.classes.features.barbarian.Rage
import org.banana_inc.mechanics.dice.d12

class Barbarian : Class(getClassFeature(1),) {

    override val details: Details = Details(
    d12,
    setOf(Ability.STRENGTH, Ability.CONSTITUTION),
    setOf(Skill.ATHLETICS, Skill.NATURE),
    setOf(WeaponProficiency.SIMPLE, WeaponProficiency.MARTIAL)
    )

    private companion object: FeatureDetails() {
        override fun getClassFeatures(): Map<Int, Set<ClassFeature>> = mapOf(
            1 to setOfNotNull<ClassFeature>(Rage()),
            )
    }

}