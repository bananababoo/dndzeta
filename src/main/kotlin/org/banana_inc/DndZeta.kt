package org.banana_inc

import co.aikar.commands.annotation.Dependency
import com.github.retrooper.packetevents.PacketEvents
import com.google.gson.Gson
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.banana_inc.util.reflection.ClassGraph
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger


open class DndZeta : JavaPlugin() {

    override fun onLoad() {
        setupGlobals()
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin))
        PacketEvents.getAPI().load()
    }

    override fun onEnable() {
        PacketEvents.getAPI().init()
        saveDefaultConfig()
        init()
    }

    fun setupGlobals(){
        plugin = this
        org.banana_inc.logger = plugin.logger
    }

    fun init(){
        for (initOnStartupClass in ClassGraph.initOnStartupClasses) {
            initOnStartupClass.objectInstance
        }
    }

    override fun onDisable() {
        PacketEvents.getAPI().terminate()
    }

}

@EventHandler
fun onLogin(e: PlayerLoginEvent){

}


@Dependency
lateinit var plugin: DndZeta
lateinit var logger: Logger
val gson = Gson()


