package org.banana_inc.util.storage

import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

val Any.tempStorage: Storage
    get() = warehouse.getOrPut(this) { Storage() }

private val warehouse: WeakHashMap<Any, Storage> = WeakHashMap()

data class StorageToken<T : Any> (val key: String): Comparable<StorageToken<T>>{
    override fun compareTo(other: StorageToken<T>): Int {
        return key.compareTo(other.key)
    }
}

data class Storage(val boxes: HashMap<StorageToken<out Any>, Any> = HashMap()) {

    fun <T: Any> containsToken(token: StorageToken<T>): Boolean{
        return this.boxes.containsKey(token)
    }
    operator fun <T: Any> set(token: StorageToken<T>, value: T){
        boxes[token] = value
    }
    inline operator fun <reified T: Any> set(key: String, value: T){
        this[StorageToken(key)] = value
    }
    inline operator fun <reified T: Any> get(key: StorageToken<T>): T? {
        return boxes[key] as? T
    }
    inline operator fun <reified T: Any> get(key: String): T? {
        return get(StorageToken(key)) as? T
    }
    inline fun <reified T: Any> putIfAbsent(key: String, value: T){
        if(get<T>(key) == null) set(key,value)
    }
    inline fun <reified T: Any> putIfAbsent(key: StorageToken<T>, value: T){
        if(get<T>(key) == null) set(key,value)
    }
    fun wipe(){
        boxes.clear()
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