@file:Suppress("unused")

package org.banana_inc.data

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.google.common.collect.HashBiMap
import org.banana_inc.data.database.UUIDBinaryDeserializer
import org.banana_inc.data.database.UUIDBinarySerializer
import org.banana_inc.item.Item
import org.banana_inc.item.items.Armor
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*
import kotlin.reflect.KClass

/*
    When adding data with subtypes that are complex, use @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
    Each impl needs a @JsonTypeName("<name>") and @JsonCreator constructor
 */

sealed class Data{

    @JsonProperty("_id")
    @BsonId
    @JsonSerialize(using = UUIDBinarySerializer::class)
    @JsonDeserialize(using = UUIDBinaryDeserializer::class)
    @SuppressWarnings("PropertyName")
    open val uuid: UUID = UUID.randomUUID()

    annotation class LoadOnStartup
    companion object {
        val dataLists: MutableMap<KClass<out Data>, HashBiMap<UUID, Data>> = mutableMapOf()
        @Suppress("unchecked_cast")
        inline fun <reified T : Data> getMap(): HashBiMap<UUID,T> {
            return dataLists[T::class] as? HashBiMap<UUID,T>? ?: error("No such DataList: ${T::class.simpleName}")
        }

        inline fun <reified T : Data> get(): MutableSet<T> {
            return getMap<T>().values
        }

        inline fun <reified T : Data> get(id: UUID): T? {
            return getMap<T>()[id]
        }
        inline fun <reified T : Data> unload(uuid: UUID) {
            dataLists[T::class]?.remove(uuid)
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    open class Inventory(
        val size: Int,
        private val items: MutableMap<Int, Item<*>> = mutableMapOf(),
        val equipment: Equipment = Equipment()
    ): Iterable<Map.Entry<Int, Item<*>>> {
        fun clear() = items.clear()
        @JsonIgnore
        operator fun set(slot: Int, item: Item<*>) {
            check(slot + 1 <= size) { "Can't set slot $slot in data inv with size $size" }
            items[slot] = item
        }
        @JsonIgnore
        operator fun get(slot: Int) = items[slot]
        @JsonIgnore
        val getAllCopy = items.toMutableMap()
        fun remove(slot: Int) = items.remove(slot)
        override fun toString() = items.toString()
        override fun iterator(): Iterator<Map.Entry<Int, Item<*>>> = items.iterator()
    }
    class Equipment(
        val armor: Armor? = null
    )
}
typealias Character = PlayerData
