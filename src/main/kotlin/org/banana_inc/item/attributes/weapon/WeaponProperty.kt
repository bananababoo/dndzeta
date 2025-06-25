package org.banana_inc.item.attributes.weapon

import org.banana_inc.util.dnd.Dice

sealed class WeaponProperty(val weaponPropertyType: WeaponPropertyType) {
    class Ammunition(val range: WeaponRange, val ammunition: org.banana_inc.item.items.Ammunition): WeaponProperty(WeaponPropertyType.AMMUNITION)
    data object Finesse: WeaponProperty(WeaponPropertyType.FINESSE)
    data object Heavy: WeaponProperty(WeaponPropertyType.HEAVY)
    data object Improvised: WeaponProperty(WeaponPropertyType.IMPROVISED)
    data object Light: WeaponProperty(WeaponPropertyType.LIGHT)
    data object Loading: WeaponProperty(WeaponPropertyType.LOADING)
    data object Range: WeaponProperty(WeaponPropertyType.RANGE)
    data object Reach: WeaponProperty(WeaponPropertyType.REACH)
    class Thrown(val range: WeaponRange): WeaponProperty(WeaponPropertyType.THROWN)
    /*
     * See https://5e.tools/items.html#lance_xphb for unless mounted
     */
    class TwoHanded(val unlessMounted: Boolean = false ): WeaponProperty(WeaponPropertyType.TWO_HANDED)
    class Versatile(val damage: Dice): WeaponProperty(WeaponPropertyType.VERSATILE)
}

enum class WeaponPropertyType {
    AMMUNITION,
    FINESSE,
    HEAVY,
    IMPROVISED,
    LIGHT,
    LOADING,
    RANGE,
    REACH,
    THROWN,
    TWO_HANDED,
    VERSATILE
}

