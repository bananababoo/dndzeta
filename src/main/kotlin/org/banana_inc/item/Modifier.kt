package org.banana_inc.item

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.Component
import org.banana_inc.extensions.changeMeta
import org.banana_inc.item.items.ItemData
import org.banana_inc.item.items.Weapon
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,       // Use a logical name for the type
    include = JsonTypeInfo.As.WRAPPER_OBJECT, // Embed as a property within the object
)
abstract class Modifier<out T: ItemData>()  {
    open fun modifyBase(item: ItemStack) {}
}

data class EnchantmentModifier(var enchantEnchantmentType: EnchantmentType, var lvl: Int): Modifier<Weapon>() {
    override fun modifyBase(item: ItemStack) {
        item.addEnchantment(org.bukkit.enchantments.Enchantment.POWER,4)
        item.changeMeta {
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }
    enum class EnchantmentType { STRONG, SPEEDY }
}

data object MagicalModifier: Modifier<ItemData>(){
    override fun modifyBase(item: ItemStack) {
        @Suppress("UnstableApiUsage")
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE,true)
    }
}

data class CustomNameModifier(val name: Component): Modifier<ItemData>() {
    override fun modifyBase(item: ItemStack) {
        item.changeMeta { displayName(name) }
    }
}

data class CustomLoreModifier(val loreLines: List<Component>): Modifier<ItemData>() {
    override fun modifyBase(item: ItemStack) {
        item.changeMeta { lore(loreLines) }
    }
}