package org.banana_inc.util.storage

import java.util.concurrent.ConcurrentSkipListMap
import kotlin.reflect.KClass

val Any.tempStorage: Storage
    get() = warehouse.getOrDefault(this, Storage())

private val warehouse: ConcurrentSkipListMap<Any, Storage> = ConcurrentSkipListMap()

data class StorageToken<T : Any> (val key: String, val clazz: KClass<T>): Comparable<StorageToken<T>>{

    override fun compareTo(other: StorageToken<T>): Int {

        return key.compareTo(other.key)
    }
}

class Storage{
    val boxes: ConcurrentSkipListMap<StorageToken<out Any>, Any> by lazy { ConcurrentSkipListMap() }
    //
    fun <T: Any> containsToken(token: StorageToken<T>): Boolean{
        return this.boxes.containsKey(token)
    }
    operator fun <T: Any> set(token: StorageToken<T>, value: T){
        if(boxes.containsKey(token)) error("Storage with id:  tried to store key ${token.key} when it already exists in storage.")
        boxes[token] = value
    }
    inline operator fun <reified T: Any> set(key: String, value: T){
        this[StorageToken(key, T::class)] = value
    }
    inline operator fun <reified T: Any> get(key: StorageToken<T>): T {
        if(!containsToken(key)) error("Storage with id: tried to get key $key doesn't exist in storage")
        return boxes[key] as T
    }
}