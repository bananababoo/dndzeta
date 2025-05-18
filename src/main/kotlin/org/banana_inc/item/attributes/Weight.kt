package org.banana_inc.item.attributes

data class Weight(var lb: Int, var oz: Double) {
    init {
        if(oz > 16){
            lb += (oz / 16).toInt()
        }
    }

    fun asLbs(): Double{
        return lb + (oz/16)
    }

    operator fun plus(other: Weight) = Weight(lb + other.lb, oz + other.oz)

}