package org.banana_inc.item.attributes.weapon

import org.banana_inc.mechanics.dice.Dice

data class WeaponData(val damageDice: Dice.Damage, val weaponMastery: WeaponMastery, val properties: HashSet<WeaponProperty>) {
    constructor(damageDice: Dice.Damage, weaponMastery: WeaponMastery, vararg properties: WeaponProperty):
            this(damageDice, weaponMastery, properties.toHashSet())


}