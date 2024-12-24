package org.banana_inc.item

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.banana_inc.plugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass


data class Item<T: ItemData> @JsonIgnore constructor(
    @JsonSerialize(using = ItemSerializer::class)  // Serialize 'type' as a string using custom serializer
    val type: T,
    val modifiers: MutableSet<Modifier<T>> = mutableSetOf(),
    var amount: Int = 1
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

    fun itemStack(): ItemStack{
        return ItemStackCreator.transform(this)
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


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
sealed class Modifier<in T: ItemData>  {
    open fun transform(item: ItemStack): ItemStack = item
    val key = NamespacedKey(plugin, this::class.simpleName!!)
    data class Enchantment(val enchantType: EnchantmentType, val lvl: Int): Modifier<ItemData.Weapon>() {
        enum class EnchantmentType {
            STRONG,
            SPEEDY
        }

        override fun transform(item: ItemStack): ItemStack {
            item.addEnchantment(org.bukkit.enchantments.Enchantment.POWER,0)
            val meta = item.itemMeta
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            item.itemMeta = meta
            return item
        }
    }
    data object Magical: Modifier<ItemData>()
}

typealias EnchantmentType = Modifier.Enchantment.EnchantmentType