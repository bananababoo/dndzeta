package org.banana_inc.data

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.bukkit.Material
import java.util.*

@SuppressWarnings("")
sealed class Data(
    @JsonProperty("_id")
    @org.mongojack.ObjectId val
    id: ObjectId = ObjectId()
) {

    data class Player(
        val uuid: UUID
    ): Data()

    data class Item(
        val material: Material
    ): Data()

}




