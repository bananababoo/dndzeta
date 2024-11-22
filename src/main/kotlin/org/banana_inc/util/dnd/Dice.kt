package org.banana_inc.util.dnd

data class Dice(val amount: Int, val sides: Int) {
    constructor(sides: Int): this(1,sides)
    open class TypedDice<T>(val dice: Map<Dice,T>){
        fun roll(){
            dice.mapValues { die -> die.key.roll() }
        }
        override fun toString(): String = dice.toString()
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
        return roll(amount,sides)
    }
}