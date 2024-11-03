package org.banana_inc.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import com.destroystokyo.paper.ParticleBuilder
import com.zorbeytorunoglu.kLib.task.Repeat
import com.zorbeytorunoglu.kLib.task.Scopes
import com.zorbeytorunoglu.kLib.task.suspendFunctionSync
import kotlinx.coroutines.launch
import org.banana_inc.extensions.relativeOffset
import org.banana_inc.plugin
import org.banana_inc.util.storage.StorageToken
import org.banana_inc.util.storage.tempStorage
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin

@CommandAlias("test")
class Command: BaseCommand() {

    private val token = StorageToken("CMD-test-taskID", Int::class)

    @Default
    fun testCommand(player: Player, x: Float, y: Float){
        val builder = ParticleBuilder(Particle.DUST).data(DustOptions(Color.GRAY,1f)).count(10).receivers(10)
        Repeat.Sync(periodTicks = 1, durationSecs = 30) {
            Scopes.defaultScope.launch {
                val offset = player.eyeLocation.relativeOffset(cos(it.getRadians()) * x, sin(it.getRadians()) * y, 1.0)
                plugin.suspendFunctionSync {
                    builder.location(offset).spawn()
                    player.sendMessage("Location: $offset")
                }
            }
        }.apply {
            player.tempStorage[token] = this.task.taskId
        }

    }
    @Subcommand("console")
    fun testConsole(){
        println("hello")
        val token = StorageToken("test", Int::class)
        3.tempStorage[token] = 7
        println((1+2).tempStorage[token])
    }

    @Subcommand("player")
    fun testConsole(player: Player){
        println("hello")
        val token = StorageToken("test", Int::class)
        println("0: ${player.tempStorage}")
        player.tempStorage[token] = 7
        println("0.5: $player")
        println("1: ${player.tempStorage}")
        println((player).tempStorage[token])
    }

}