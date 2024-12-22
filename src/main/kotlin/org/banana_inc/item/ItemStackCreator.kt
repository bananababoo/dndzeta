package org.banana_inc.item

import net.kyori.adventure.text.Component
import org.banana_inc.extensions.component
import org.banana_inc.extensions.useMeta
import org.banana_inc.plugin
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemStackCreator {

    val idKey = NamespacedKey.fromString("id", plugin)!!
    val modifiersKey = NamespacedKey.fromString("modifiers", plugin)!!

    fun transform(item: Item<*>): ItemStack {
        var base = transform(item.type)
        base.amount = item.amount
        base.useMeta {
            this.setCustomModelData(item.type.id)
            persistentDataContainer[idKey, PersistentDataType.INTEGER] = item.type.id
            persistentDataContainer[modifiersKey, PersistentDataType.STRING] = plugin.gson.toJson(item.modifiers)
        }
        for(modifier in item.modifiers) {
            base = modifier.transform(base)
        }
        return base
    }

    private fun transform(itemData: ItemData): ItemStack {
        val item = ItemStack(Material.NETHERITE_HOE)
        val meta = item.itemMeta

        meta.displayName(itemData.name.component)
        meta.lore(figureOutLore(itemData))
        meta.persistentDataContainer[NamespacedKey.fromString("id", plugin)!!, PersistentDataType.INTEGER] = itemData.id

        item.itemMeta = meta
        return item
    }

    private fun figureOutLore(itemData: ItemData): List<Component>{
        return mutableListOf<Component>().apply {
            when(itemData) {
                is ItemData.Weapon -> {
                    add("Damage: ${itemData.damageDice}".component)
                } else -> Unit
            }

            add("Weight: ${itemData.weight}".component)
            add("Cost: ${itemData.cost}".component)
        }
    }


}