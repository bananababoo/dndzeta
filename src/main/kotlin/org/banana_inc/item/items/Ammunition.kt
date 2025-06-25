package org.banana_inc.item.items

import org.banana_inc.extensions.CP
import org.banana_inc.extensions.SP
import org.banana_inc.extensions.lb
import org.banana_inc.extensions.oz
import org.banana_inc.item.ItemMaterial
import org.banana_inc.item.attributes.Currency
import org.banana_inc.item.attributes.Weight

sealed class Ammunition(currency: Currency, weight: Weight): ItemData(currency, weight){
    override val itemType = ItemMaterial.AMMO
    data object Arrow : Ammunition(5.CP, 0.8.oz)
    data object Bolt : Ammunition(5.CP, 1.2.oz)
    data object FirearmBullet : Ammunition(3.SP, .5.lb)
    data object Needle : Ammunition(2.CP, .32.oz)
    data object SlingBullet : Ammunition(4.CP, 1.2.oz)
}