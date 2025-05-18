package org.banana_inc.item

import net.kyori.adventure.text.Component
import org.banana_inc.extensions.component
import org.banana_inc.extensions.useMeta
import org.banana_inc.item.data.ItemData
import org.banana_inc.item.data.Weapon
import org.banana_inc.plugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.annotations.NotNull
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

object ItemStackCreator {

    val idKey = NamespacedKey.fromString("id", plugin)!!
    val modifiersKey = NamespacedKey.fromString("modifiers", plugin)!!

    fun makeBaseItem(@NotNull item: Item<*>): ItemStack {
        val base = ItemStack(item.type.itemType.material)
        val itemData = item.type
        base.amount = item.amount

        base.useMeta {
            persistentDataContainer[idKey, PersistentDataType.STRING] = item.type.name
            persistentDataContainer[modifiersKey, PersistentDataType.STRING] = plugin.gson.toJson(item.modifiers)
        }

        applyVisuals(itemData, base)
        applyAttributes(itemData, base)

        for(modifier in item.modifiers) {
            modifier.modifyBase(base)
        }

        return base
    }

    private fun applyAttributes(itemData: ItemData, base: ItemStack){
        base.useMeta {
            setMaxStackSize(itemData.stackSize)
        }
    }

    private fun applyVisuals(itemData: ItemData, base: ItemStack) {
        base.useMeta {
            itemModel = NamespacedKey("dndzeta", figureOutResourcePackKey(itemData))
            displayName( //from-words-like-this -> To Words Like This
                ("<reset>" + itemData.name.split("_").joinToString(" ")
                    { word -> word.replaceFirstChar { it.titlecaseChar() }
                }).component
            )
            lore(figureOutLore(itemData, base))
        }
    }

    private fun figureOutLore(itemData: ItemData, base: ItemStack): List<Component>{
        return mutableListOf<Component>().apply {
            when(itemData) {
                is Weapon -> {
                    add("Damage: ${itemData.damageDice}".component)
                } else -> Unit
            }

            add("Weight: %.2f lbs.".format(itemData.weight.asLbs() * base.amount).component)
            add("Value: ${itemData.value}".component)
        }
    }

    private fun figureOutResourcePackKey(itemData: ItemData): String{
        var currentClass: KClass<*> = itemData::class
        var path = itemData.name.replace(" ", "_").lowercase()
        while(currentClass != ItemData::class) {
            for (superclass in currentClass.superclasses) {
                if(superclass.isSubclassOf(ItemData::class)) {
                    currentClass = superclass
                    if(currentClass == ItemData::class) break
                    path = "${superclass.simpleName!!.lowercase()}/$path"
                }
            }
        }
        return path
    }

}