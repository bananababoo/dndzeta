package org.banana_inc.item

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlin.reflect.KClass

data class Item<T: ItemData> @JsonIgnore constructor(
    @JsonSerialize(using = ItemSerializer::class)  // Serialize 'type' as a string using custom serializer
    val type: T,
    val modifiers: MutableSet<Modifier<T>> = mutableSetOf(),
) {
    @get:JsonIgnore
    val magical: Boolean
        get() = type is ItemData.Magical || modifiers.any { it is Modifier.Magical }
    @JsonCreator
    constructor(
        @JsonProperty("type") @JsonDeserialize(using = ItemDeserializer::class)
        type: KClass<out T>,
        modifiers: MutableSet<Modifier<T>> = mutableSetOf()
    ): this(ItemData[type.java], modifiers)
}


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
sealed class Modifier<in T: ItemData>  {
    data class Enchantment(val enchantType: EnchantmentType, val lvl: Int): Modifier<ItemData.Weapon>() {
        enum class EnchantmentType {
            STRONG,
            SPEEDY
        }
    }
    data object Magical: Modifier<ItemData>()
}

typealias EnchantmentType = Modifier.Enchantment.EnchantmentType