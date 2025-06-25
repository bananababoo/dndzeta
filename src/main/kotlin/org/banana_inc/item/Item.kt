package org.banana_inc.item

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.banana_inc.extensions.getFirstTypeArgumentClass
import org.banana_inc.item.items.ItemData
import org.banana_inc.item.items.Magical
import org.banana_inc.logger
import org.banana_inc.util.collections.ClassSet
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

data class Item<T: ItemData> @JsonIgnore constructor(
    @JsonSerialize(using = ItemSerializer::class)
    val type: T,
    private var modifiers: ClassSet<Modifier<T>> = ClassSet(),
    @JsonProperty("amount")
    private var _amount: Int = 1
){
    var amount: Int
        get() = _amount
        set(value) {
            require(value > 0) { "item amount must be positive, currently: $value." }
            _amount = value
        }

    @get:JsonIgnore
    val magical: Boolean
        get() = type is Magical || modifiers.any { it is MagicalModifier }

    @JsonCreator
    constructor(
        @JsonProperty("type") @JsonDeserialize(using = ItemDeserializer::class)
        type: KClass<out T>,
        modifiers: MutableSet<Modifier<T>> = mutableSetOf()
    ): this(ItemData[type.java], modifiers = ClassSet<Modifier<T>>().apply{
        for (mod in modifiers) {
            logger.info("checking $type vs ${mod::class.getFirstTypeArgumentClass}")
            val modSpecifier = mod::class.getFirstTypeArgumentClass
            check(type.isSubclassOf(modSpecifier) || type == modSpecifier)
        }
        addAll(modifiers)
    } )

    fun itemStack(): ItemStack{
        return ItemStackCreator.makeBaseItem(this)
    }

    fun addModifier(mod: Modifier<*>){
        check(checkIfModifierValid(mod)) { "Item $this cannot add moidfer $mod because $type is not equal to or a subclass of ${mod::class}" }
        @Suppress("unchecked_cast")
        modifiers.add(mod as Modifier<T>)
    }
    fun removeModifier(mod: Modifier<*>){
        @Suppress("unchecked_cast")
        modifiers.removeClass(mod::class as KClass<Modifier<T>>)
    }
    fun getModifiers(): Set<Modifier<T>>{
        return modifiers.toSet()
    }

    fun checkIfModifierValid(mod: Modifier<*>): Boolean {
        val modSpecifier = mod::class.getFirstTypeArgumentClass
        return type::class.isSubclassOf(modSpecifier) || type::class == modSpecifier
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