package org.banana_inc.data

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOneModel
import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.launch
import org.banana_inc.logger
import java.util.*
import kotlin.reflect.full.hasAnnotation

object DatabaseActions {
    // Used to load data from the database, into server memory
    // should be used on server startup, and admin reload commands

    fun loadAll() {
        for (dataClass in Data::class.sealedSubclasses) {
            Data.serverDataLists[dataClass] =
                if (dataClass.hasAnnotation<Data.LoadOnStartup>())
                    Database.getCollection(dataClass).find().filterIsInstance<Data>().toMutableSet()
                else
                    mutableSetOf<Data>().apply { logger.info("database loaded ${dataClass.simpleName}") }
        }
    }

    inline fun <reified T : Data> load() {
        Data.serverDataLists[T::class] = Database.getCollection<T>()
            .find().filterIsInstance<Data>().toMutableSet()
    }

    inline fun <reified T : Data> load(uuid: UUID): T? {
        val data: List<T> = Database.getCollection<T>().find(Filters.eq("_id", uuid)).filterIsInstance<T>()
        if (data.isEmpty()) return null
        else if (data.size > 1) error("UHHH OHHHHHHHHHHHHH multiple items with same uuid")
        Data.serverDataLists[T::class] = data.toMutableSet()
        return data[0]
    }

    inline fun <reified T : Data> save() {
        update(*(Data.serverDataLists[T::class] as MutableSet<Data>).toTypedArray())
    }

    fun saveAll() {
        Data.serverDataLists.values.forEach {
            update(*it.toTypedArray())
        }
    }

    inline fun <reified T : Data> store(vararg objects: T) {
        check(objects.isNotEmpty()) { "You can't insert no objects into the database" }
        val result = Database.getCollection<T>().insertMany(objects.toList())
        checkNotNull(result) { "null when inserting collection $objects into collection ${T::class.simpleName}" }
        check(result.wasAcknowledged()) { "database insert not acknowledged when inserting collection $objects into collection ${T::class.simpleName}" }
        val data: MutableSet<Data> = Data.serverDataLists[T::class]
            ?: throw IllegalStateException("Tried to get DataList that doesn't exist (shouldn't be possible) ${T::class.simpleName}")
        data.addAll(objects)
    }

    private inline fun <reified T : Data> add(obj: Array<out T>) {
        if (obj.isEmpty()) return
        val data: MutableSet<Data> = Data.serverDataLists[T::class]
            ?: throw IllegalStateException("Tried to get DataList that doesn't exist (shouldn't be possible) ${T::class.simpleName}")
        data.addAll(obj)
    }

    inline fun <reified T : Data> update(vararg objects: T) {
        check(objects.isNotEmpty()) { "You can't insert no objects into the database" }
        val collection = Database.getCollection<T>()
        val operations = objects.map {
            ReplaceOneModel(Filters.eq("_id", it.uuid), it)
        }
        val result = collection.bulkWrite(operations)
        check(result.wasAcknowledged()) {
            "Database insert not acknowledged when inserting collection $objects into collection ${T::class.simpleName}"
        }
    }

    inline fun <reified T : Data> updateThenAsync(vararg objects: T, crossinline arg: () -> Unit) {
        Scopes.ioScope.launch {
            update(*objects)
            arg()
        }
    }
}