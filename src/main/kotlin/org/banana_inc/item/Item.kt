package org.banana_inc.item

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.banana_inc.item.data.ItemData
import org.banana_inc.item.data.Magical
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass


data class Item<T: ItemData> @JsonIgnore constructor(
    @JsonSerialize(using = ItemSerializer::class)  // Serialize 'type' as a string using custom serializer
    val type: T,
    val modifiers: MutableSet<Modifier<T>> = mutableSetOf(),
    @JsonProperty("amount")
    private var _amount: Int = 1
) {
    var amount: Int
        get() = _amount
        set(value) {
            if (value <= 0) {
                throw IllegalStateException("item amount can't be $value")
            }
            _amount = value
        }

    @get:JsonIgnore
    val magical: Boolean
        get() = type is Magical || modifiers.any { it is Modifier.Magical }
    @JsonCreator
    constructor(
        @JsonProperty("type") @JsonDeserialize(using = ItemDeserializer::class)
        type: KClass<out T>,
        modifiers: MutableSet<Modifier<T>> = mutableSetOf()
    ): this(ItemData[type.java], modifiers)

    fun itemStack(): ItemStack{
        return ItemStackCreator.makeBaseItem(this)
    }

    fun equalBesidesAmount(other: Item<*>): Boolean{
        if(this.type != other.type)
            return false
        if(this.modifiers != other.modifiers)
            return false
        return true
    }

    override fun toString(): String {
        return "$type:$amount" + modifiers.ifEmpty { "" }
    }
}