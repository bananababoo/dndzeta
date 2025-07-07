package org.banana_inc.item.attributes

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlin.properties.Delegates

class Currency(amount: Long) {
    @get:JsonIgnore
    var amount: Long by Delegates.observable(0) { prop, old, new ->
        textForm = getString()
    }
    var textForm = getString()

    init {
        this.amount = amount
    }

    companion object{
        fun copper(amount: Long) = Currency(amount * Type.COPPER.value)
        fun silver(amount: Long) = Currency(amount * Type.SILVER.value)
        fun electrum(amount: Long) = Currency(amount * Type.SILVER.value * 5)
        fun gold(amount: Long) = Currency( amount * Type.GOLD.value)
        fun platinum(amount: Long) = Currency( amount * Type.GOLD.value * 10)
    }

    enum class Type(val symbol: String,val color: String, val value: Int){
        COPPER("⛀","<#854c13>",1),
        SILVER("⛀","<#8a8783>",10),
        GOLD("⛁","<gold>",100),
    }

    fun getString(): String{
        var total = amount
        val strings = mutableListOf<String>()
        for(entry in Type.entries.reversed()){
            if(total < 0 || (total / entry.value).toInt() == 0) continue
            strings.add("${entry.color} ${total / entry.value} ${entry.symbol}")
            total -= (total / entry.value).toInt() * entry.value
        }
        return strings.joinToString(separator = " ", postfix = "<reset>")
    }

    override fun toString(): String { return textForm}
}
