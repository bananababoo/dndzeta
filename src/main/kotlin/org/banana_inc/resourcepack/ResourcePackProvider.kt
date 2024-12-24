package org.banana_inc.resourcepack

import com.google.api.client.http.InputStreamContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.Permission
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.zorbeytorunoglu.kLib.task.Scopes
import com.zorbeytorunoglu.kLib.task.delaySync
import kotlinx.coroutines.launch
import me.bananababoo.dndzeta.BuildConfig
import net.kyori.adventure.key.Key
import org.banana_inc.EventManager
import org.banana_inc.extensions.component
import org.banana_inc.item.ItemData
import org.banana_inc.logger
import org.banana_inc.plugin
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import team.unnamed.creative.BuiltResourcePack
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.base.Writable
import team.unnamed.creative.model.*
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter
import team.unnamed.creative.texture.Texture
import java.io.ByteArrayInputStream
import java.io.File

@InitOnStartup
object ResourcePackProvider {

    private lateinit var hash: String
    private lateinit var fileId: String

    init {
        reloadResourcePack()
        EventManager.addListener<PlayerJoinEvent> {
            applyResourcePack(player)
        }
    }

    private fun reloadResourcePack(apply: Boolean = false) {
        getDriveService()

        val resourcePack = updateResourcePack()
        ByteArrayInputStream(resourcePack.data().toByteArray()).use {
            addOrReplaceFile(getDriveService(), it)
        }
        if(apply) {
            for(p in Bukkit.getOnlinePlayers()){
                applyResourcePack(p)
            }
        }

    }

    fun reloadResourcePackAsync(apply: Boolean = false) {
        Scopes.ioScope.launch {
            reloadResourcePackAsync(apply)
        }
    }

    private fun addOrReplaceFile(
        driveService: Drive,
        byteArrayInputStream: ByteArrayInputStream,
    ) {
        val fileName = "pack.zip" // The desired file name
        val contentType = "application/zip" // MIME type for a zip file

        // Step 1: Search for the existing file
        val query = StringBuilder("name = '$fileName' and trashed = false")

        val existingFiles = driveService.files().list()
            .setQ(query.toString())
            .setFields("files(id, name)")
            .execute()
            .files

        // Step 2: Create InputStreamContent from the ByteArrayInputStream
        val contentStream = InputStreamContent(contentType, byteArrayInputStream)

        if (existingFiles.isNotEmpty()) {
            // Replace the existing file
            val existingFileId = existingFiles[0].id
            driveService.files().update(existingFileId, null, contentStream).execute()
            fileId = existingFileId
            println("Replaced existing file: $fileName (ID: $existingFileId)")
        } else {
            // Add a new file
            val fileMetadata = com.google.api.services.drive.model.File().apply {
                name = fileName
            }
            val newFile = driveService.files().create(fileMetadata, contentStream).execute()
            fileId = newFile.id
            println("Created new file: $fileName (ID: ${newFile.id})")
        }

        val permission = Permission().apply {
            type = "anyone"  // This means the file will be accessible to anyone on the internet
            role = "reader"  // Read-only access, allowing anyone to view it
        }
        driveService.permissions().create(fileId, permission).execute()

        // Generate and print the file URL
        val fileUrl = "https://drive.google.com/file/d/$fileId/view?usp=sharing"
        println("Resource Pack URL: $fileUrl")
    }

    private fun getDriveService(): Drive {
        val credentials = GoogleCredentials
            .fromStream(ByteArrayInputStream(BuildConfig.gdriveAuth.toByteArray()))
            .createScoped(listOf(DriveScopes.DRIVE))

        val httpTransport = NetHttpTransport()

        val jsonFactory = GsonFactory.getDefaultInstance()
        return Drive.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName("Drive API Kotlin")
            .build()
    }


    private fun updateResourcePack(): BuiltResourcePack{
        val resourcePackBuilder = ResourcePack.resourcePack()
        resourcePackBuilder.packMeta(42, "resourcePack".component) // https://minecraft.wiki/w/Pack_format

        val resourcePackFolder = File(plugin.dataFolder, "resource_pack")
        val images = File(resourcePackFolder, "img")
        images.mkdirs()

        val hoeModel = Model.model()
            .key(Key.key("minecraft","item/netherite_hoe"))
            .parent(Key.key("minecraft","item/handheld"))
            .textures(
                ModelTextures.builder().layers(
                    ModelTexture.ofKey(Key.key("minecraft","item/netherite_hoe"))
                ).build()
            )

        for(image in images.walk()){
            if(image.isFile){

                val newFileName = image.nameWithoutExtension
                    .lowercase()
                    .replace(" ", "_")
                    .replace(Regex("[^a-z0-9_.-]"), "")
                val clazz = ItemData.getClasses().find { it.simpleName.equals(image.nameWithoutExtension, ignoreCase = true) } ?: continue
                val id = ItemData[clazz].id

                resourcePackBuilder.texture(
                    Texture.texture()
                        .key(Key.key("dndzeta", "item/$newFileName.${image.extension}"))
                        .data(Writable.file(image))
                        .build()
                )

                val itemKey = Key.key("dndzeta", "item/$newFileName")

                resourcePackBuilder.model(Model.model()
                    .key(itemKey)
                    .parent(Key.key("minecraft","item/handheld"))
                    .textures(
                        ModelTextures.builder().layers(
                            ModelTexture.ofKey(itemKey)
                        ).build()
                    )
                    .build()
                )

                hoeModel.addOverride(
                    ItemOverride.of(
                        itemKey,
                        ItemPredicate.customModelData(id)
                    )
                )
            }
        }
        resourcePackBuilder.model(hoeModel
            .build()
        )
        val resourcePack = MinecraftResourcePackWriter.minecraft().build(resourcePackBuilder)
        hash = resourcePack.hash()
        return resourcePack
    }

    fun applyResourcePack(player: Player) {
        plugin.delaySync(1L) {
            logger.info("Applying resource pack to $player https://drive.google.com/uc?export=download&id=$fileId")
            player.setResourcePack("https://drive.google.com/uc?export=download&id=$fileId", hash)
        }
    }

}