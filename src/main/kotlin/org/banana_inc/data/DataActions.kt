package org.banana_inc.data

import com.google.gson.Gson

object DataActions {
    var gson = Gson()

    // Used to load data from the database, into server memory
    // should be used on server startup, and admin reload commands
    fun loadAllData() {
        for (dataClass in Data::class.sealedSubclasses) {
            ServerData.serverDataLists[dataClass] = ServerData(Database.getCollection(dataClass)
                .find().filterIsInstance<Data>().toMutableList())
        }
    }

    inline fun <reified T: Data> loadData() {
        ServerData.serverDataLists[T::class] = ServerData(Database.getCollection<T>()
            .find().filterIsInstance<Data>().toMutableList())
    }

    inline fun <reified T : Data> store(vararg objects: T){
        check(objects.isNotEmpty()) { "You can't insert no objects into the database" }
        val result = Database.getCollection<T>().insertMany(objects.toList())
        checkNotNull(result) { "null when inserting collection $objects into collection ${T::class.simpleName}" }
        check(result.wasAcknowledged()) { "database insert not acknowledged when inserting collection $objects into collection ${T::class.simpleName}" }
        ServerData.add(objects)
    }

    inline fun <reified T: Data> get(): List<T>{
        val data: ServerData<Data> = ServerData.serverDataLists[T::class] ?: throw IllegalStateException("Tried to get DataList that doesn't exist (shouldn't be possible) ${T::class.simpleName}")
        return data.list.filterIsInstance(T::class.java).toMutableList()
    }
}