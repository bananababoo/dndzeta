package org.banana_inc.item.items

import org.banana_inc.extensions.GP
import org.banana_inc.extensions.lb
import org.banana_inc.item.attributes.Currency
import org.banana_inc.item.attributes.Weight
import org.banana_inc.item.attributes.armor.ArmorACValue

sealed class Armor(currency: Currency, weight: Weight, val armorClass: ArmorACValue, val stealthDisadvantage: Boolean): ItemData(currency, weight)  {
    sealed class Light(currency: Currency, weight: Weight, armorClass: Int, stealthDisadvantage: Boolean=false): Armor(currency, weight,ArmorACValue.Calculated(armorClass), stealthDisadvantage) {
        data object PaddedArmor: Light(5.GP, 8.lb, 11, true)
        data object LeatherArmor: Light(10.GP, 10.lb, 11)
        data object StuddedLeatherArmor: Light(45.GP, 13.lb, 12)
    }
    sealed class Medium(currency: Currency, weight: Weight, armorClass: Int, stealthDisadvantage: Boolean=false): Armor(currency, weight,ArmorACValue.Calculated(armorClass, maxModifier = 2), stealthDisadvantage) {
        data object HideArmor: Medium(10.GP, 12.lb, 12)
        data object ChainShirt: Medium(50.GP, 20.lb, 13)
        data object ScaleMail: Medium(50.GP, 45.lb, 14, true)
        data object Breastplate: Medium(400.GP, 20.lb, 14)
        data object HalfPlateArmor: Medium(750.GP, 40.lb, 15, true)
    }
    sealed class Heavy(currency: Currency, weight: Weight,  armorClass: Int, stealthDisadvantage: Boolean, val strengthMin: Int? = null): Armor(currency, weight, ArmorACValue.Fixed(armorClass), stealthDisadvantage=stealthDisadvantage) {
        data object RingMail: Heavy(30.GP, 40.lb, 14, true)
        data object ChainMail: Heavy(75.GP, 55.lb, 16, true, 13)
        data object SplintArmor: Heavy(200.GP, 60.lb, 17, true, 15)
        data object PlateArmor: Heavy(1500.GP, 65.lb, 18, true, 15)
    }
    data object Shield: Armor(10.GP, 6.lb, ArmorACValue.Additional(2), false)
}
