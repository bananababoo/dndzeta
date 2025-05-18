package org.banana_inc.extensions

import com.google.common.collect.HashBiMap
import java.util.*

inline fun <reified R> HashBiMap<UUID, *>.filterIsInstance(): HashBiMap<UUID, R> {
    return filterIsInstanceTo<R, HashBiMap<UUID, R>>(HashBiMap.create())
}

inline fun <reified R, C : HashBiMap<UUID, in R>> HashBiMap<UUID, *>.filterIsInstanceTo(destination: C): C {
    for ((uuid,value) in this) if (value is R) destination.put(uuid ,value)
    return destination
}
