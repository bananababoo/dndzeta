package org.banana_inc.data.attributes

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.stream.Collectors


class MapAttribute<T,U>(@JsonIgnore private val value: Map<T,U> = mutableMapOf()): Attribute<Map<T,U>>() {

    override fun get(): Map<T,U>{
        return modifiers.values.stream()
            .flatMap { it.entries.stream() }
            .collect(Collectors.toMap(
                { it.key },
                { it.value },
                { old, new -> merge(old, new) })
            )
    }

    operator fun get(key: T): U?{
        return get()[key]
    }

}