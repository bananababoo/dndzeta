package org.banana_inc.extensions

import org.banana_inc.item.attributes.Currency
import org.banana_inc.item.attributes.Weight

/**
 * Returns a random integer between specified integers.
 * @param from From
 * @param to To
 * @return A random integer between two parameters.
 */
fun random(from: Int, to: Int): Int = (from..to).random()

val Int.CP
    get() = Currency.copper(this.toLong())
val Int.SP
    get() = Currency.silver(this.toLong())
val Int.EP
    get() = Currency.electrum(this.toLong())
val Int.GP
    get() = Currency.gold(this.toLong())
val Int.PP
    get() = Currency.platinum(this.toLong())

operator fun Currency.plus(value: Int): Currency {
    return Currency(amount + value)
}

operator fun Currency.minus(value: Int): Currency {
    check(amount - value > 0)
    return Currency(amount - value)
}

operator fun Currency.times(value: Int): Currency {
    return Currency(amount * value)
}

val Double.oz get() = Weight(0,this)
val Double.lb get() = Weight(this.toInt(), ((this % 1) * 16))
val Int.oz get() = Weight(0,this.toDouble())
val Int.lb get() = Weight(this,0.0)

