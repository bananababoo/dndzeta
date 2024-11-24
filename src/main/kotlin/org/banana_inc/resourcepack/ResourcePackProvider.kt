package org.banana_inc.resourcepack

import com.zorbeytorunoglu.kLib.task.Scopes
import com.zorbeytorunoglu.kLib.task.delaySync
import kotlinx.coroutines.launch
import me.bananababoo.dndzeta.BuildConfig
import net.kyori.adventure.key.Key
import org.apache.commons.net.ftp.FTPClient
import org.banana_inc.EventManager
import org.banana_inc.extensions.component
import org.banana_inc.item.ItemData
import org.banana_inc.logger
import org.banana_inc.plugin
import org.banana_inc.util.initialization.InitOnStartup
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

    private val client = FTPClient()

    private lateinit var hash: String

    init {
        reloadResourcePack()
        EventManager.addListener<PlayerJoinEvent> {
            applyResourcePack(it.player)
        }
    }

    fun reloadResourcePack(){
        Scopes.ioScope.launch {
            client.connect("ftpupload.net")
            client.login(BuildConfig.ftpUsername, BuildConfig.ftpPassword)
            val resourcePack = updateResourcePack()
            client.setFileType(FTPClient.BINARY_FILE_TYPE)
            ByteArrayInputStream(resourcePack.data().toByteArray()).use {
                val success = client.storeFile("htdocs/resource-pack/pack.zip", it)
                if (success) {
                    println("File stored successfully on FTP server")
                } else {
                    println("Failed to store file on FTP server")
                }
            }
            client.logout()
            client.disconnect()
        }
    }


    private fun updateResourcePack(): BuiltResourcePack{
        val resourcePackBuilder = ResourcePack.resourcePack()
        resourcePackBuilder.packMeta(42, "resourcePack".component) // https://minecraft.wiki/w/Pack_format

        val resourcePackFolder = File(plugin.dataFolder, "resource_pack")
        val images = File(resourcePackFolder, "img")
        images.mkdirs()

        val hoeModel = Model.model().key(Key.key("minecraft","item/netherite_hoe"))
        for(image in images.walk()){
            if(image.isFile){

                val newFileName = image.nameWithoutExtension
                    .lowercase()
                    .replace(" ", "_")
                    .replace(Regex("[^a-z0-9_.-]"), "")
                val key = Key.key("dndzeta", newFileName)

                val clazz = ItemData.getClasses().find { it.simpleName.equals(image.nameWithoutExtension, ignoreCase = true) } ?: continue
                val id = ItemData[clazz].id

                resourcePackBuilder.texture(
                    Texture.texture()
                        .key(key)
                        .data(Writable.file(image))
                        .build()
                )

                resourcePackBuilder.model(Model.model()
                    .key(key)
                    .textures(
                        ModelTextures.builder().layers(
                            ModelTexture.ofKey(key)
                        ).build()
                    )
                    .build()
                )

                hoeModel.addOverride(
                    ItemOverride.of(
                        key,
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
            logger.info("Applying resource pack to $player http://dndzeta.ct.ws/resource-pack/pack.zip")
            player.setResourcePack("http://dndzeta.ct.ws/resource-pack/pack.zip", hash)
        }
    }

}