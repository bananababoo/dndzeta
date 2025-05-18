package org.banana_inc.resourcepack

import net.kyori.adventure.key.Key
import org.banana_inc.extensions.component
import org.banana_inc.item.data.ItemData
import org.banana_inc.plugin
import team.unnamed.creative.BuiltResourcePack
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.base.Writable
import team.unnamed.creative.model.Model
import team.unnamed.creative.model.ModelTexture
import team.unnamed.creative.model.ModelTextures
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter
import team.unnamed.creative.texture.Texture
import java.io.File

object ResourcePackGenerator {
    fun getResourcePack(): BuiltResourcePack {
        val resourcePackBuilder = ResourcePack.resourcePack()
        resourcePackBuilder.packMeta(42, "resourcePack".component) // https://minecraft.wiki/w/Pack_format

        val resourcePackFolder = File(plugin.dataFolder, "resource_pack")
        val images = File(resourcePackFolder, "img")
        images.mkdirs()

        for(image in images.walk()){
            if(image.isFile){
                val newFileName = image.nameWithoutExtension
                    .lowercase()
                    .replace(" ", "_")
                    .replace(Regex("[^a-z0-9_.-]"), "")
                val clazz = ItemData.getClasses().find{
                    it.simpleName.equals(image.nameWithoutExtension, ignoreCase = true)
                } ?: continue

                val data = ItemData[clazz]

                resourcePackBuilder.texture(
                    Texture.texture()
                        .key(Key.key("dndzeta", "item/$newFileName.${image.extension}"))
                        .data(Writable.file(image))
                        .build()
                )

                resourcePackBuilder.model(
                    Model.model()
                    .key( Key.key("dndzeta", "item/${data.name}"))
                    .parent(Key.key("minecraft","item/handheld"))
                    .textures(
                        ModelTextures.builder().layers(
                            ModelTexture.ofKey(Key.key("dndzeta", "item/${data.name}"))
                        ).build()
                    )
                    .build()
                )

                //waiting on https://github.com/unnamed/creative/issues/74
            }
        }

        val resourcePack = MinecraftResourcePackWriter.minecraft().build(resourcePackBuilder)
        return resourcePack
    }


}