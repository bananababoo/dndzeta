package org.banana_inc.item.attributes.armor

import org.banana_inc.mechanics.classes.Ability

sealed class ArmorACValue {
    data class Fixed(val baseAC: Int): ArmorACValue()
    data class Calculated(val baseAC: Int, val modifier: Ability=Ability.DEXTERITY, val maxModifier: Int?=null): ArmorACValue()
    data class Additional(val addon: Int): ArmorACValue()
}