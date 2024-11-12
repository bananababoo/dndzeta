package org.banana_inc.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.bson.codecs.pojo.annotations.BsonId
import org.bukkit.Bukkit
import java.util.*
import kotlin.reflect.KClass

sealed class Data(
    @JsonProperty("_id")
                  @BsonId
                  @JsonSerialize(using = UUIDBinarySerializer::class)
                  @JsonDeserialize(using = UUIDBinaryDeserializer::class)
                  @SuppressWarnings("PropertyName")
                  open val uuid: UUID = UUID.randomUUID()
) {
    companion object {
        val serverDataLists: MutableMap<KClass<out Data>, MutableSet<Data>> = mutableMapOf()
        inline fun <reified T : Data> get(): List<T> {
            return serverDataLists[T::class]!!.filterIsInstance<T>()
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
        private var inventory: Map<Int, String> = mutableMapOf()
    ) : Data(){
        @DatabaseUpdateHandler("money")
        fun updateMoney(newMoney: Long){
            Bukkit.getPlayer(uuid)!!.sendMessage("money changed: $newMoney")
        }

        fun getItem(slot: Int){
            get<Item>()[inventory[slot]!!]
        }
    }

    operator fun List<Item>.get(namedID: String){
        this.find { it.namedID == namedID }
    }

    @LoadOnStartup
    open class Item(
        open val namedID: String = "Unnamed Item",
        val shiny: Boolean = false
    ): Data(){


        data class Sword(
            val damage: Long,
            override val namedID: String
        ): Item(namedID)
    }

}





