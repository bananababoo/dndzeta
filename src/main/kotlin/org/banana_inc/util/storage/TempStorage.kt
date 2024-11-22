package org.banana_inc.util.storage

import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

val Any.tempStorage: Storage
    get() = warehouse.getOrPut(this) { Storage() }

private val warehouse: WeakHashMap<Any, Storage> = WeakHashMap()

data class StorageToken<T : Any> (val key: String, val clazz: KClass<T>): Comparable<StorageToken<T>>{
    override fun compareTo(other: StorageToken<T>): Int {
        return key.compareTo(other.key)
    }
}

data class Storage(val boxes: HashMap<StorageToken<out Any>, Any> = HashMap()){

    fun <T: Any> containsToken(token: StorageToken<T>): Boolean{
        return this.boxes.containsKey(token)
    }
    operator fun <T: Any> set(token: StorageToken<T>, value: T){
        boxes[token] = value
    }
    inline operator fun <reified T: Any> set(key: String, value: T){
        this[StorageToken(key, T::class)] = value
    }
    inline operator fun <reified T: Any> get(key: StorageToken<T>): T? {
        return (boxes[key] ?: return null) as T
    }

    inline operator fun <reified T: Any> get(key: String): T? {
        return get(StorageToken(key, T::class))
    }

}

class PropertyDelegate<T, V>: ReadWriteProperty<T, V> {
    private val values = mutableMapOf<T, V>()

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return values.getValue(thisRef)
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        values[thisRef] = value
    }
}

fun <T, V> extraProperty() = PropertyDelegate<T, V>()