package org.banana_inc.item.attributes

class Currency private constructor(val amount: Long, val type: Type){
    companion object{
        fun copper(amount: Long) = Currency(amount, Type.COPPER)
        fun silver(amount: Long) = Currency(amount, Type.SILVER)
        fun electrum(amount: Long) = Currency(amount, Type.ELECTRUM)
        fun gold(amount: Long) = Currency( amount, Type.GOLD)
        fun platinum(amount: Long) = Currency( amount, Type.PLATINUM)
    }

    enum class Type(val acronym: String){
        COPPER("cp"),
        SILVER("sp"),
        ELECTRUM("ep"),
        GOLD("gp"),
        PLATINUM("pp")
    }
    override fun toString(): String{
        return "$amount ${type.acronym}"
    }
}
