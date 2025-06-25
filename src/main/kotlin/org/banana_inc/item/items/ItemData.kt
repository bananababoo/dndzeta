package org.banana_inc.item.items

import com.google.common.collect.MutableClassToInstanceMap
import org.banana_inc.item.Item
import org.banana_inc.item.ItemMaterial
import org.banana_inc.item.Modifier
import org.banana_inc.item.attributes.Currency
import org.banana_inc.item.attributes.Weight
import org.banana_inc.util.collections.ClassSet
import org.banana_inc.util.initialization.InitOnStartup
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@InitOnStartup
sealed class ItemData(
    val value: Currency,
    val weight: Weight,
){
    /**
     * **When adding a Item Category (eg Spell Component), it must extend Category**
     */
    interface Category

    open val itemType: ItemMaterial = ItemMaterial.ITEM
    open val stackSize = 64
    var name: String

    companion object {
        private val items = MutableClassToInstanceMap.create<ItemData>()

        private val itemNameToItem: HashMap<String, ItemData> by lazy {
            val map = HashMap<String, ItemData>()
            for(i in items){
                map[i.value.name] = i.value
            }
            map
        }

        val sortedItemClasses: List<Class<out ItemData>> by lazy {
            items.keys.sortedBy{ it.simpleName }
        }

        operator fun <T: ItemData> get(id: Class<T>): T {
            return id.cast(items[id] ?: error("No such item: $id"))
        }

        operator fun get(id: String): ItemData? {
            return itemNameToItem[id]
        }

        fun getAll(): Set<ItemData> {
            return items.values.toSet()
        }

        fun getClasses(): Set<Class<out ItemData>> {
            return items.keys
        }
        fun getKClasses(): Set<KClass<out ItemData>> {
            return getClasses().map{ a -> a.kotlin }.toSet()
        }
    }

    init {
        items[this.javaClass] = this
        this.name = this.javaClass.simpleName.replace(Regex("(?<=[a-zA-Z])(?=[A-Z])"), "_").lowercase()
    }

    inline fun <reified T : ItemData> T.create(modifiers: MutableSet<Modifier<T>> = mutableSetOf()): Item<T> {
        for(modifier in modifiers) {
            val superType = modifier::class.java.genericSuperclass as ParameterizedType
            val itemModifierBroadType = (superType.actualTypeArguments[0] as Class<*>).kotlin
            check(T::class.isSubclassOf(itemModifierBroadType) || T::class == itemModifierBroadType){
                "An item type $this was attempted to be created with incompatible modifier $modifier of category $itemModifierBroadType"
            }
        }
        @Suppress("unchecked_cast")
        return Item(this, ClassSet<Modifier<T>>().apply{
            addAll(modifiers)
        })
    }

}