package org.banana_inc.item

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore.lore
import net.kyori.adventure.text.Component
import org.banana_inc.extensions.changeMeta
import org.banana_inc.extensions.minimessage
import org.banana_inc.item.items.ItemData
import org.banana_inc.item.items.Weapon
import org.banana_inc.logger
import org.bukkit.inventory.ItemStack

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,      // Use a logical name for the type
    include = JsonTypeInfo.As.WRAPPER_OBJECT, // Embed as a property within the object
)
abstract class Modifier<out T: ItemData>()  {
    open fun modifyBase(item: ItemStack) {}
}

/**
 * first argument must be @JsonProperty("list") List<T>
 */
abstract class MultiModifier<out T: ItemData, L: Any>(var list: MutableList<L>, @JsonIgnore val complex: Boolean): Modifier<T>(){
    override fun toString(): String {
        return this::class.simpleName + ":" + list.toString()
    }

    override fun equals(other: Any?): Boolean {
        if(other is MultiModifier<T, L>) {
            logger.info("${other.list}, compared to $list")
            return list == other.list
        }
        return false
    }

    override fun hashCode(): Int {
        var result = complex.hashCode()
        result = 31 * result + list.hashCode()
        return result
    }
}

class EnchantmentModifier(@JsonProperty("list") enchantments: MutableList<Enchantment>): MultiModifier<Weapon, Enchantment>(enchantments, true) {
    override fun modifyBase(item: ItemStack) {
        item.addEnchantment(org.bukkit.enchantments.Enchantment.MENDING,1)
        @Suppress("UnstableApiUsage")
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE,true)
    }
    override fun toString(): String {
        return "EnchantmentModifier:$list)"
    }
}

data class Enchantment(var enchantment: Type, var lvl: Int) {
    enum class Type { STRONG, SPEEDY }
}

data object MagicalModifier: Modifier<ItemData>(){
    override fun modifyBase(item: ItemStack) {
        @Suppress("UnstableApiUsage")
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE,true)
    }

    override fun toString(): String {
        return "Magical"
    }
}

data class CustomNameModifier(val name: Component): Modifier<ItemData>() {
    override fun modifyBase(item: ItemStack) {
        item.changeMeta { displayName(name) }
    }

    override fun toString(): String {
        return "Name: ${name.minimessage}"
    }
}

class CustomLoreModifier(@JsonProperty("list") loreLines: MutableList<Component>): MultiModifier<ItemData, Component>(loreLines, false){
    override fun modifyBase(item: ItemStack) {
        @Suppress("UnstableApiUsage")
        item.setData(DataComponentTypes.LORE,lore().addLines(list))
    }

    override fun toString(): String {
        return list.joinToString(prefix = "Lore:[", postfix = "]", separator = ", ") { it.minimessage }
    }
}