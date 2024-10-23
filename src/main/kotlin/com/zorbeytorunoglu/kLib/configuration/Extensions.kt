package com.zorbeytorunoglu.kLib.configuration

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * Creates a file in plugin's data folder or copies if a file with the same name exists in resources.
 * @param fileName Name of the file with its extension.
 * @return Resource object.
 */
fun JavaPlugin.createYamlResource(fileName: String): Resource = Resource(this, fileName)

/**
 * Creates a file in plugin's data folder or copies if a file with the same name exists in resources and loads it.
 * @param fileName Name of the file with its extension.
 * @return Loaded version of YamlConfiguration of the Resource.
 */
fun JavaPlugin.createLoadYamlResource(fileName: String): YamlConfiguration =
    YamlConfiguration.loadConfiguration(Resource(this, fileName).file)

/**
 * Creates a file in plugin's data folder or copies if a file with the same name exists in resources.
 * @param path Path of the file.
 * @param fileName Name of the file with its extension.
 * @return Resource object.
 */
fun JavaPlugin.createFileWithPath(path: String, fileName: String): Resource = Resource(path, fileName)

fun JavaPlugin.createResource(file: File): Resource = Resource(file)