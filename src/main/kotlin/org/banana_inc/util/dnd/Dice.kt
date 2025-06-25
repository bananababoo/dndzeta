package org.banana_inc.util.dnd

import org.banana_inc.extensions.enumReadableName

class Dice(val amount: Int, private val sides: Sides) {

    constructor(sides: Sides): this(1, sides)

    open class TypedDice<T: Enum<*>>(private val dice: Map<Dice,T>) {
        fun roll(){
            dice.mapValues { die -> die.key.roll() }
        }
        override fun toString(): String = dice.entries.joinToString(separator = "<newline>") {
            val type = it.value.enumReadableName()
            val dice = "${if(it.key.sides.sides == 1) it.key.amount else it.key}"
            "$dice $type"
        }
    }

    class Damage(vararg dice: Pair<Dice,DamageType>): TypedDice<DamageType>(mapOf(*dice))

    companion object{
        private val random = java.util.Random()
        fun roll(amount: Int, sides: Int): Long{
            var total = 0L
            for(i in 1..amount){
                total += random.nextInt(1, sides)
            }
            return total
        }
    }

    fun roll(): Long{
        return roll(amount, sides.sides)
    }

    enum class Sides(val sides: Int, val diceNotation: String){
        ONE(1, "d1"), //damage dice for blowgun is 1, this is not used anywhere else in the SRD
        FOUR(4, "d4"),
        SIX(6, "d6"),
        EIGHT(8, "d8"),
        TEN(10, "d10"),
        TWELVE(12, "d12"),
        TWENTY(20, "d20"),
        ONE_HUNDRED(100, "d100")
    }

    @Override
    override fun toString(): String {
        return "$amount${sides.diceNotation}"
    }
}
val d4 get() = Dice(Dice.Sides.FOUR)
val d6 get() = Dice(Dice.Sides.SIX)
val d8 get() = Dice(Dice.Sides.EIGHT)
val d10 get() = Dice(Dice.Sides.TEN)
val d12 get() = Dice(Dice.Sides.TWELVE)
val d20 get() = Dice(Dice.Sides.TWENTY)
val d100 get() = Dice(Dice.Sides.ONE_HUNDRED)

val Int.d4: Dice get() = Dice(this, Dice.Sides.FOUR)
val Int.d6: Dice get() = Dice(this, Dice.Sides.SIX)

val Int.d8: Dice get() = Dice(this, Dice.Sides.EIGHT)
val Int.d10: Dice get() = Dice(this, Dice.Sides.TEN)
val Int.d12: Dice get() = Dice(this, Dice.Sides.TWELVE)
val Int.d20: Dice get() = Dice(this, Dice.Sides.TWENTY)
val Int.d100: Dice get() = Dice(this, Dice.Sides.ONE_HUNDRED)










