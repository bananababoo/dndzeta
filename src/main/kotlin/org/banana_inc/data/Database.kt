package org.banana_inc.data

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.changestream.ChangeStreamDocument
import com.mongodb.client.model.changestream.FullDocument
import com.mongodb.client.model.changestream.FullDocumentBeforeChange
import com.mongodb.client.model.changestream.OperationType
import com.zorbeytorunoglu.kLib.task.Scopes
import kotlinx.coroutines.launch
import me.bananababoo.dndzeta.BuildConfig
import org.banana_inc.item.ItemDeserializer
import org.banana_inc.item.ItemSerializer
import org.banana_inc.item.data.ItemData
import org.banana_inc.logger
import org.banana_inc.util.initialization.InitOnStartup
import org.banana_inc.util.reflection.FieldReflection
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
    private var database: MongoDatabase
    private val objectMapper: JsonMapper = JsonMapper.builder()
        .configure(MapperFeature.USE_GETTERS_AS_SETTERS, true).build()
        .apply {
            registerModule(JavaTimeModule())
            registerKotlinModule {
                enable(KotlinFeature.StrictNullChecks)
                enable(KotlinFeature.KotlinPropertyNameAsImplicitName)
            }
            registerModules(
                SimpleModule().apply {
                    addSerializer(ItemData::class.java, ItemSerializer())  // Register serializer
                    addDeserializer(KClass::class.java, ItemDeserializer())
                }
            )
        }
    private val collections: MutableSet<String>  = mutableSetOf()

    init {
        val connectionString = "mongodb+srv://${BuildConfig.databaseUsername}:${BuildConfig.databasePassword}@cluster0.j6wxz.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
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

            postInit()
        }
    }

    private fun postInit() {
        enablePreImagesAll()
        DatabaseActions.loadAll()
        val session = client.startSession()
        Data.dataLists.keys.forEach { serverDataList ->
            val col = getCollection(serverDataList)
            val insertChanges = serverDataList.hasAnnotation<Data.LoadOnStartup>()
            Scopes.ioScope.launch {
                val changeStream = col.watch(session).fullDocument(FullDocument.UPDATE_LOOKUP).fullDocumentBeforeChange(FullDocumentBeforeChange.REQUIRED)
                logger.info("Watching changes for $serverDataList")
                try {
                    changeStream.forEach { change ->
                        logger.info("Processing change: $change")
                        Scopes.ioScope.launch  {
                            handleOperation(change, insertChanges, serverDataList)
                        }
                    }
                } catch (streamException: Exception) {
                    logger.severe("Error in change stream processing in here")
                    throw streamException
                }
            }
        }
    }

    private infix fun <T> T.replaceWith(other: T) where T : Any {
        this::class.members
            .filterIsInstance<kotlin.reflect.KMutableProperty<*>>()
            .forEach { property ->
                val value = property.getter.call(other)
                property.setter.call(this, value)
            }
    }

    private fun handleOperation(change: ChangeStreamDocument<out Data>, insertChanges: Boolean, serverDataList: KClass<out Data>) {
        val list = Data.dataLists[serverDataList]!!.values
        val newDoc: Data = change.fullDocument!!
        logger.info("doing change with type ${change.operationType}")
        when (change.operationType) {
            OperationType.INSERT -> if (insertChanges) list.add(newDoc)
            OperationType.UPDATE -> {
                list.find { it.uuid == newDoc.uuid }?.let {currentDoc ->
                    currentDoc replaceWith newDoc
                    for (updatedField in change.updateDescription!!.updatedFields!!) {
                        callEvents(currentDoc,change.fullDocumentBeforeChange!!,serverDataList, updatedField.key)
                    }
                }
            }
            OperationType.REPLACE -> {
                list.find {it.uuid == newDoc.uuid }?.let { currentDoc ->
                    change.fullDocument
                    currentDoc replaceWith newDoc
                    for (updatedField in FieldReflection.getDifferences(newDoc, change.fullDocumentBeforeChange!!)) {
                        callEvents(currentDoc,change.fullDocumentBeforeChange!!,serverDataList, updatedField)
                    }
                }
            }
            OperationType.DELETE -> list.remove(change.fullDocument)
            else -> { return }
        }
    }

    private fun callEvents(data: Data, oldData: Data, dataClass: KClass<out Data>, changedProperty: String){
        for(function in dataClass.declaredMemberFunctions){
            if(function.hasAnnotation<DatabaseUpdateListener>()){
                val updateHandler = function.findAnnotation<DatabaseUpdateListener>()!!
                if(changedProperty.matches(Regex("^${updateHandler.propertyName}.*"))){
                    val args = mutableListOf<Any>()
                    args.add(data)
                    if(updateHandler.includeOldData) {
                        args.add(
                            dataClass.declaredMemberProperties.find
                            { it.name == changedProperty.split(".")[0] }!!
                                .getter
                                .call(oldData)!!
                        )
                    }
                    function.call(*args.toTypedArray())
                }
            }
        }
    }

    inline fun <reified T : Data> getCollection(): JacksonMongoCollection<T> {
        return getCollection(T::class)
    }

    fun <T : Data> getCollection(obj: KClass<T>): JacksonMongoCollection<T> {
        val name = obj.simpleName!!
        val collection = JacksonMongoCollection.builder()
            .withObjectMapper(objectMapper)
            .build(database, name, obj.java, UuidRepresentation.STANDARD)
        if(name !in collections){
            enablePreImages(name)
        }
        return collection
    }

    private fun enablePreImages(collectionName: String){
        collections.add(collectionName)
        val collModCommand = Document("collMod", collectionName)
            .append("changeStreamPreAndPostImages", Document("enabled", true))
        database.runCommand(collModCommand)
        logger.info("Enabled pre-images for collection: $collectionName")
    }
    private fun enablePreImagesAll(){
        val collectionList: List<String> = database.listCollectionNames().into(ArrayList())
        for (collectionName in collectionList) {
            collections.add(collectionName)
            val collModCommand = Document("collMod", collectionName)
                .append("changeStreamPreAndPostImages", Document("enabled", true))
            database.runCommand(collModCommand)
            logger.info("Enabled pre-images for collection: $collectionName")
        }
    }


}
