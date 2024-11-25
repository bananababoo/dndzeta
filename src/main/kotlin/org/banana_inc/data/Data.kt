package org.banana_inc.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
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

    companion object {
        val serverDataLists: MutableMap<KClass<out Data>, MutableSet<Data>> = mutableMapOf()
        inline fun <reified T : Data> get(): List<T> {
            return serverDataLists[T::class]?.filterIsInstance<T>() ?: error("No such DataList: ${T::class.simpleName}")
        }
        inline fun <reified T : Data> get(id: UUID): T? {
            return get<T>().find { it.uuid == id }
        }
        inline fun <reified T : Data> unload(uuid: UUID) {
            serverDataLists[T::class]?.remove(get<T>(uuid)!!)
        }
    }

    annotation class LoadOnStartup
    data class Player(
        @JsonProperty("_id")
        @BsonId
        @JsonSerialize(using = UUIDBinarySerializer::class)
        @JsonDeserialize(using = UUIDBinaryDeserializer::class)
        override val uuid: UUID,
        var money: Long = 0,
        var inventory: MutableMap<Int, Item<*>> = mutableMapOf()
    ) : Data(){
        @DatabaseUpdateListener("money")
        fun updateMoney(){
            Bukkit.getPlayer(uuid)!!.sendMessage("money changed: $money")
        }
        @DatabaseUpdateListener("inventory", includeOldData = true)
        fun updateInventory(old: Map<Int, Item<*>>){
            Bukkit.getPlayer(uuid)!!.sendMessage("inventoryChanged changed: $inventory")
            Bukkit.getPlayer(uuid)!!.sendMessage("old inventory: $old")
        }

    }
}


