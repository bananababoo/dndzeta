package org.banana_inc.data

import kotlin.reflect.KClass

class ServerData<T : Data>(val list: MutableList<T>) {

    companion object{

        val serverDataLists: MutableMap<KClass<out Data>,ServerData<Data>> = mutableMapOf()

        inline fun <reified T: Data> add(obj: Array<out T>){
            if(obj.isEmpty()) return
            val data: ServerData<Data> = serverDataLists[T::class] ?: throw IllegalStateException("Tried to get DataList that doesn't exist (shouldn't be possible) ${T::class.simpleName}")
            data.list.addAll(obj)
        }
    }

}
