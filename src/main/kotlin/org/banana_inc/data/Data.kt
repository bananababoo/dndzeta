@file:Suppress("unused")

package org.banana_inc.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.google.common.collect.HashBiMap
import org.banana_inc.config.ServerConfig
import org.banana_inc.item.Item
import org.bson.codecs.pojo.annotations.BsonId
import org.bukkit.Bukkit
import java.util.*
import kotlin.reflect.KClass

/*
    When adding data with subtypes that are , use @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
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

    data class Player(
        @JsonProperty("_id")
        @BsonId
        @JsonSerialize(using = UUIDBinarySerializer::class)
        @JsonDeserialize(using = UUIDBinaryDeserializer::class)
        override val uuid: UUID,
        var money: Long = 0,
        var inventory: Inventory = Inventory(36),
        var settings: Settings = Settings(),
    ) : Data(){
        @JsonIgnore
        val localData = Local()

        data class Settings(
            val resourcePackOptions: MutableSet<ServerConfig.ResourcePackConfig.Data> = mutableSetOf()
        )

        @DatabaseUpdateListener("money")
        fun updateMoney(){
            Bukkit.getPlayer(uuid)!!.sendMessage("money changed: $money")
        }
        @DatabaseUpdateListener("inventory", includeOldData = true)
        fun updateInventory(old: Map<Int, Item<*>>){
            Bukkit.getPlayer(uuid)!!.sendMessage("inventoryChanged changed: $inventory")
            Bukkit.getPlayer(uuid)!!.sendMessage("old inventory: $old")
        }


        /**
         * This Data does not need to be saved, and can be safely removed on login
         */
        data class Local(
            var inGUI: Boolean = false
        )
    }
    data class Inventory(
        val size: Int,
        val items: MutableMap<Int, Item<*>> = mutableMapOf(),
    ){
        fun clear() = items.clear()
        @JsonIgnore
        operator fun set(slot: Int, item: Item<*>) {
            if(slot + 1 > size) throw IllegalStateException("Can't set slot $slot in data inv with size $size")
            items[slot] = item
        }
        @JsonIgnore
        operator fun get(slot: Int) = items[slot]
        fun remove(slot: Int) = items.remove(slot)
        override fun toString() = items.toString()
    }
}


