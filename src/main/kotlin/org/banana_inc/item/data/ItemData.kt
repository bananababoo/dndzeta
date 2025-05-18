package org.banana_inc.item.data

import com.google.common.collect.MutableClassToInstanceMap
import org.banana_inc.item.Item
import org.banana_inc.item.ItemMaterial
import org.banana_inc.item.attributes.Currency
import org.banana_inc.item.attributes.Weight
import org.banana_inc.logger
import org.banana_inc.util.initialization.InitOnStartup
import kotlin.reflect.KClass

@InitOnStartup
sealed class ItemData(
    val value: Currency,
    val weight: Weight,
){

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


        init {
            logger.info("companion called")
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

    fun <T : ItemData> T.create(): Item<T> = Item(this)

}