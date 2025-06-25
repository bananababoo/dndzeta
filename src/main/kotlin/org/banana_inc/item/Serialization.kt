package org.banana_inc.item

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.banana_inc.item.items.ItemData
import org.banana_inc.logger
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

class ComponentSerializer: JsonSerializer<Component>() {
    override fun serialize(value: Component, gen: JsonGenerator, serializers: SerializerProvider) {
        val json = GsonComponentSerializer.gson().serialize(value)
        gen.writeString(json)
    }
}

class ComponentDeserializer : JsonDeserializer<Component>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Component {
        logger.info(p.text)
        val gotit = GsonComponentSerializer.gson().deserialize(p.text)
        return gotit
    }
}