package org.banana_inc.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.banana_inc.plugin
import java.util.*
import javax.naming.ConfigurationException

class ServerConfig(
    val resource_packs: ResourcePackConfig
) {
    data class ResourcePackConfig(
        val default: Data,
        val music: Data
    ) {
        data class Data(
            val url: String,
            val hash: String,
            val primary: Boolean,
            @JsonIgnore
            val id: UUID = UUID.randomUUID()
        )
    }

    companion object {
        val mapper = YAMLMapper().registerKotlinModule()
        fun reload() {
            config = mapper.readValue(plugin.dataPath.resolve("config.yml").toFile(), ServerConfig::class.java)
        }
        private fun validateProperties(config: ServerConfig) {
            if(config.resource_packs.music.hash.length != 20)
                throw ConfigurationException("Resource Pack (Music) hash must be 20 characters ")
            if(config.resource_packs.default.hash.length != 20)
                throw ConfigurationException("Resource Pack (Default) hash must be 20 characters ")
        }
    }
}

var config: ServerConfig = ServerConfig.mapper.readValue(plugin.dataPath.resolve("config.yml").toFile())