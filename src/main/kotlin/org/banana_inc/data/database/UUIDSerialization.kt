package org.banana_inc.data.database

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.nio.ByteBuffer
import java.util.*

class UUIDBinarySerializer : JsonSerializer<UUID>() {
    override fun serialize(value: UUID, gen: JsonGenerator, serializers: SerializerProvider) {
        val buffer = ByteBuffer.wrap(ByteArray(16))
        buffer.putLong(value.mostSignificantBits)
        buffer.putLong(value.leastSignificantBits)
        gen.writeBinary(buffer.array())  // Write binary directly as BSON Binary type
    }
}

class UUIDBinaryDeserializer : JsonDeserializer<UUID>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): UUID {
        val bytes = p.binaryValue  // Reads BSON Binary value as a byte array
        val buffer = ByteBuffer.wrap(bytes)
        val mostSigBits = buffer.long
        val leastSigBits = buffer.long
        return UUID(mostSigBits, leastSigBits)
    }
}
