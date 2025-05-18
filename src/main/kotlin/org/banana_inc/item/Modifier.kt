package org.banana_inc.item

import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.kyori.adventure.text.TextComponent
import org.banana_inc.extensions.changeMeta
import org.banana_inc.item.data.ItemData
import org.banana_inc.item.data.Weapon
import org.banana_inc.plugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
sealed class Modifier<in T: ItemData>  {
    open fun modifyBase(item: ItemStack) {}
    val key = NamespacedKey(plugin, this::class.simpleName!!)
    data class Enchantment(val enchantType: EnchantmentType, val lvl: Int): Modifier<Weapon>() {
        enum class EnchantmentType {
            STRONG,
            SPEEDY
        }

        override fun modifyBase(item: ItemStack) {
            item.addEnchantment(org.bukkit.enchantments.Enchantment.POWER,0)
            item.changeMeta {
                addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
        }
    }

    data object Magical: Modifier<ItemData>()

    data class CustomName(val name: TextComponent): Modifier<ItemData>() {
        override fun modifyBase(item: ItemStack) {
            item.changeMeta { displayName(name) }
        }
    }

    data class CustomLore(val loreLines: List<TextComponent>): Modifier<ItemData>() {
        override fun modifyBase(item: ItemStack) {
            item.changeMeta { lore(loreLines) }
        }
    }
}

typealias EnchantmentType = Modifier.Enchantment.EnchantmentType