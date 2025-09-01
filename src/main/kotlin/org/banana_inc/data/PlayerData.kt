package org.banana_inc.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.banana_inc.config.ServerConfig
import org.banana_inc.data.attributes.PlayerAttributes
import org.banana_inc.data.database.DatabaseUpdateListener
import org.banana_inc.data.database.UUIDBinaryDeserializer
import org.banana_inc.data.database.UUIDBinarySerializer
import org.banana_inc.extensions.syncDataToInventory
import org.banana_inc.item.Item
import org.banana_inc.mechanics.classes.classes.Class
import org.bson.codecs.pojo.annotations.BsonId
import org.bukkit.Bukkit
import java.util.*

data class PlayerData(
    @JsonProperty("_id") @BsonId
    @JsonSerialize(using = UUIDBinarySerializer::class)
    @JsonDeserialize(using = UUIDBinaryDeserializer::class)
    override val uuid: UUID,
    val inventory: Inventory = Inventory(36),
    val settings: Settings = Settings(),
    @JsonProperty("classes")
    private val classes: MutableSet<Class> = mutableSetOf(),
    val attributes: PlayerAttributes = PlayerAttributes()
) : Data(){

    @JsonIgnore
    val localData = Local()

    init {
       updateAttributes()
    }

    fun modify(action: PlayerData.() -> Unit){
        action(this)
        updateAttributes()
    }

    fun addClass(`class`: Class){
        modify {
            classes.add(`class`)
        }
    }

    fun updateAttributes(){
        classes.forEach { `class` ->
            `class`.modifyAttributes(attributes)
            `class`.unlockedFeatures.forEach { classFeature ->
                classFeature.modifyAttributes(attributes)
            }
        }
    }

    @DatabaseUpdateListener("inventory", includeOldData = true)
    fun updateInventory(old: Map<Int, Item<*>>){
        player.sendMessage("inventoryChanged changed: $inventory")
        player.sendMessage("old inventory: $old")
        player.inventory.syncDataToInventory()
    }

    data class Settings(
        val resourcePackOptions: MutableSet<ServerConfig.ResourcePackConfig.Data> = mutableSetOf()
    )

    /**
     * This Data does not need to be saved
     */
    data class Local(
        var inGUI: Boolean = false
    )

    private val player get() = Bukkit.getPlayer(uuid)?: error("Player not found: $uuid")
}