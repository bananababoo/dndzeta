package org.banana_inc.data.database

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
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
import net.kyori.adventure.text.Component
import org.banana_inc.data.Data
import org.banana_inc.item.ComponentDeserializer
import org.banana_inc.item.ComponentSerializer
import org.banana_inc.item.ItemSerializer
import org.banana_inc.item.items.ItemData
import org.banana_inc.logger
import org.banana_inc.util.initialization.InitOnStartup
import org.banana_inc.util.reflection.FieldReflection
import org.bson.Document
import org.bson.UuidRepresentation
import org.mongojack.JacksonMongoCollection
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@InitOnStartup
object Database {
    private var client: MongoClient
    private var database: MongoDatabase
    val objectMapper: JsonMapper = jsonMapper {
        addModule(JavaTimeModule())
        configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, true)
        addModule(
            kotlinModule {
                enable(KotlinFeature.KotlinPropertyNameAsImplicitName)
                enable(KotlinFeature.SingletonSupport)
            }
        )
        defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL,JsonInclude.Include.USE_DEFAULTS))
        addModule(SimpleModule().apply {
            addSerializer(ItemData::class.java, ItemSerializer())  // Register serializer
            addSerializer(Component::class.java, ComponentSerializer())
            addDeserializer(Component::class.java, ComponentDeserializer())
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
        if (result.getInteger("ok") == 1) {
            postInit()
        }
    }

    private fun postInit() {
        DatabaseActions.loadAll()
        enablePreImagesAll()
        val session = client.startSession()
        Data.Companion.dataLists.keys.forEach { serverDataList ->
            val col = getCollection(serverDataList)
            val insertChanges = serverDataList.hasAnnotation<Data.LoadOnStartup>()
            Scopes.ioScope.launch {
                val changeStream = col.watch(session).fullDocument(FullDocument.UPDATE_LOOKUP).fullDocumentBeforeChange(
                    FullDocumentBeforeChange.REQUIRED)
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
            .filterIsInstance<KMutableProperty<*>>()
            .forEach { property ->
                val value = property.getter.call(other)
                property.setter.call(this, value)
            }
    }

    private fun handleOperation(change: ChangeStreamDocument<out Data>, insertChanges: Boolean, serverDataList: KClass<out Data>) {
        val list = Data.Companion.dataLists[serverDataList]!!.values
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
        logger.info("getting collection ${obj.simpleName}")
        val name = obj.simpleName!!
        if(!database.listCollectionNames().contains(name)){
            database.createCollection(name)
        }
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