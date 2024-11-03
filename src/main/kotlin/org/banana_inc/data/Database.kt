package org.banana_inc.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.changestream.OperationType
import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.bananababoo.dnd_zeta.BuildConfig
import org.banana_inc.util.initialization.RegistrationLock
import org.bson.Document
import org.bson.UuidRepresentation
import org.bukkit.entity.Player
import org.mongojack.JacksonMongoCollection
import kotlin.reflect.KClass


object Database {
    lateinit var client: MongoClient
    lateinit var database: MongoDatabase
    var players: Map<Player, Data.Player> = mutableMapOf()
    var items: List<Data.Item> = mutableListOf()
    val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule()) // Register the module for handling UUID and other types
    }

    fun init() {
        RegistrationLock.register(this)
        val connectionString =
            "mongodb+srv://banana:${BuildConfig.databasePassword}@cluster0.j6wxz.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        val serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build()
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(connectionString))
            .serverApi(serverApi)
            .build()
        client = MongoClients.create(mongoClientSettings)
        database = client.getDatabase("dnd_zeta")
        // Check connection (ping MongoDB)
        runBlocking {
            database.runCommand(Document("ping", 1)).run {
                if(this.getInteger("ok") == 0){
                    postInit()
                }
            }
        }

    }
    private fun postInit(){
        DataActions.loadAllData()
        for (serverDataList in ServerData.serverDataLists) {
            val list = serverDataList.value.list
            val col = getCollection(serverDataList.key)
            Scopes.ioScope.launch {
                val changeStream = col.watch()
                changeStream.forEach(){ change ->
                    checkNotNull(change.fullDocument)
                    when(change.operationType){
                        OperationType.INSERT -> list.add(change.fullDocument)
                        OperationType.UPDATE,OperationType.REPLACE  -> list[list.indexOfFirst{old -> old.id == change.fullDocument.id }] = change.fullDocument
                        OperationType.DELETE -> list.remove(change.fullDocument)
                        else -> return@forEach
                    }
                    println("Received a change event: $change")
                }
            }

        }
    }

    inline fun <reified T : Data> getCollection(): JacksonMongoCollection<T> {
        val name: String = requireNotNull(T::class.simpleName)
        return JacksonMongoCollection.builder()
            .withObjectMapper(objectMapper)
            .build(database, name, T::class.java,UuidRepresentation.STANDARD)!!
    }

    inline fun <reified T : Data> getCollection(obj: T): JacksonMongoCollection<T> {
        val name: String = requireNotNull(obj::class.simpleName)
        return JacksonMongoCollection.builder()
            .withObjectMapper(objectMapper)
            .build(database, name,T::class.java,UuidRepresentation.STANDARD)
    }

    fun <T : Data> getCollection(obj: KClass<T>): JacksonMongoCollection<T> {
        val name: String = requireNotNull(obj.simpleName)
        return JacksonMongoCollection.builder()
            .withObjectMapper(objectMapper)
            .build(database, name,obj.java,UuidRepresentation.STANDARD)
    }


}







