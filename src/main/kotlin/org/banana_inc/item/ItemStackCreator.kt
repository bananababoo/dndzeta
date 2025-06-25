package org.banana_inc.item

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.banana_inc.data.Database
import org.banana_inc.extensions.capitalizeFirstLetter
import org.banana_inc.extensions.component
import org.banana_inc.extensions.useMeta
import org.banana_inc.item.items.ItemData
import org.banana_inc.item.items.Weapon
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
            val value = Database.objectMapper.writerFor(jacksonTypeRef<MutableSet<Modifier<*>>>()).writeValueAsString(item.getModifiers())
            persistentDataContainer[modifiersKey, PersistentDataType.STRING] = value
        }

        applyVisuals(itemData, base)
        applyAttributes(itemData, base)

        for(modifier in item.getModifiers()) {
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
                itemData.name.split("_").joinToString(" ")
                    { word -> word.capitalizeFirstLetter() }
                .component.decorations(mapOf(TextDecoration.ITALIC to TextDecoration.State.FALSE))
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