package org.banana_inc.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.changestream.ChangeStreamDocument
import com.mongodb.client.model.changestream.FullDocument
import com.mongodb.client.model.changestream.OperationType
import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.launch
import me.bananababoo.dnd_zeta.BuildConfig
import org.banana_inc.logger
import org.banana_inc.util.initialization.InitOnStartup
import org.bson.Document
import org.bson.UuidRepresentation
import org.mongojack.JacksonMongoCollection
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@InitOnStartup
object Database {
    private var client: MongoClient
    var database: MongoDatabase
    val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        registerModule(KotlinModule.Builder().enable(KotlinFeature.StrictNullChecks).build())
    }

    init {
        val connectionString = "mongodb+srv://banana:${BuildConfig.databasePassword}@cluster0.j6wxz.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(connectionString))
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
            .build()
        client = MongoClients.create(mongoClientSettings)
        database = client.getDatabase("dnd_zeta")

        val result = database.runCommand(Document("ping", 1))
        logger.info("1 $result")
        if (result.getInteger("ok") == 1) {
            logger.info("2")
            postInit()
        }
    }

    private fun postInit() {
        DatabaseActions.loadAll()
        val session = client.startSession()

        Data.serverDataLists.keys.forEach { serverDataList ->
            val col = getCollection(serverDataList)
            val insertChanges = serverDataList.hasAnnotation<Data.LoadOnStartup>()
            Scopes.ioScope.launch {
                val changeStream = col.watch(session).fullDocument(FullDocument.UPDATE_LOOKUP)
                changeStream.forEach { change ->
                    checkNotNull(change.fullDocument)
                    logger.info("change: ${change.fullDocument}")
                    handleOperation(change, insertChanges, serverDataList)
                }
            }
        }
    }

    private fun handleOperation(change: ChangeStreamDocument<out Data>, insertChanges: Boolean, serverDataList: KClass<out Data>) {
        val list = Data.serverDataLists[serverDataList]!!
        val doc: Data = change.fullDocument ?: return
        when (change.operationType) {
            OperationType.INSERT -> if (insertChanges) list.add(change.fullDocument)
            OperationType.UPDATE, OperationType.REPLACE -> {
                list.find { it.uuid == doc.uuid }?.let {
                    list.remove(it)
                    list.add(doc)
                    callEvents(doc, serverDataList)
                }
            }
            OperationType.DELETE -> list.remove(change.fullDocument)
            else -> {}
        }
        println("Received a change event: $change")
    }

    private fun callEvents(data: Data, dataClass: KClass<out Data>){
        for(function in dataClass.declaredMemberFunctions){
            if(function.hasAnnotation<DatabaseUpdateHandler>()){
                val updateHandler = function.findAnnotation<DatabaseUpdateHandler>()!!
                val property = dataClass.declaredMemberProperties.find { it.name == updateHandler.propertyName }!!
                val propertyValue = property.getter.call(data)
                function.call(data, propertyValue)
            }
        }
    }

    inline fun <reified T : Data> getCollection(): JacksonMongoCollection<T> {
        return JacksonMongoCollection.builder()
            .withObjectMapper(objectMapper)
            .build(database, T::class.simpleName!!, T::class.java, UuidRepresentation.STANDARD)!!
    }

    inline fun <reified T : Data> getCollection(obj: T): JacksonMongoCollection<T> {
        return JacksonMongoCollection.builder()
            .withObjectMapper(objectMapper)
            .build(database, obj::class.simpleName!!, T::class.java, UuidRepresentation.STANDARD)
    }

    fun <T : Data> getCollection(obj: KClass<T>): JacksonMongoCollection<T> {
        return JacksonMongoCollection.builder()
            .withObjectMapper(objectMapper)
            .build(database, obj.simpleName!!, obj.java, UuidRepresentation.STANDARD)
    }
}
