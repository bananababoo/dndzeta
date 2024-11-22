package org.banana_inc.item

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import kotlin.reflect.KClass

class ItemSerializer : JsonSerializer<ItemData>() {
    override fun serialize(value: ItemData, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value::class.simpleName)
    }
}

class ItemDeserializer : JsonDeserializer<KClass<out ItemData>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): KClass<out ItemData> {
        val className = p.text  // Read the class name as a string
        return ItemData.getClasses()
            .firstOrNull { it.kotlin.simpleName == className }?.kotlin
            ?: throw IllegalArgumentException("Unknown type: $className")  // Handle error if type isn't found
    }
}