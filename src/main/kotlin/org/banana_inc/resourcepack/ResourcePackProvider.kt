package org.banana_inc.resourcepack

import com.zorbeytorunoglu.kLib.task.Scopes
import com.zorbeytorunoglu.kLib.task.delaySync
import kotlinx.coroutines.launch
import org.banana_inc.EventManager
import org.banana_inc.logger
import org.banana_inc.plugin
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import java.io.ByteArrayInputStream

@InitOnStartup
object ResourcePackProvider {

    lateinit var hash: String
    lateinit var fileId: String

    init {
        reloadResourcePack()
        EventManager.addListener<PlayerJoinEvent> {
            applyResourcePack(player)
        }
    }

    private fun reloadResourcePack(apply: Boolean = false) {
        val googleDrive = GoogleDrive.getDriveService()

        val resourcePack = ResourcePackGenerator.getResourcePack()
        ByteArrayInputStream(resourcePack.data().toByteArray()).use {
            GoogleDrive.addOrReplaceFile(googleDrive, it)
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

    fun applyResourcePack(player: Player) {
        plugin.delaySync(1L) {
            logger.info("Applying resource pack to $player https://drive.google.com/uc?export=download&id=$fileId")
            player.setResourcePack("https://drive.google.com/uc?export=download&id=$fileId", hash)
        }
    }





}