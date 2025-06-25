package org.banana_inc.item.items

import org.banana_inc.extensions.*
import org.banana_inc.item.attributes.Currency

sealed class Coin(currency: Currency): ItemData(currency, .32.oz){
    data object CopperPiece : Coin(1.CP)
    data object SilverPiece : Coin(1.SP)
    data object ElectrumPiece : Coin(1.EP)
    data object GoldPiece : Coin(1.GP)
    data object PlatinumPiece : Coin(1.PP)
}