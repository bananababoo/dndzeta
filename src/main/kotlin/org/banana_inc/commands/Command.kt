package org.banana_inc.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import com.destroystokyo.paper.ParticleBuilder
import com.zorbeytorunoglu.kLib.task.CancelableTask
import com.zorbeytorunoglu.kLib.task.Repeat
import io.papermc.paper.event.player.AsyncChatEvent
import org.banana_inc.EventManager
import org.banana_inc.data.DatabaseActions
import org.banana_inc.extensions.asyncNearbyPlayers
import org.banana_inc.extensions.data
import org.banana_inc.extensions.relativeOffset
import org.banana_inc.util.storage.StorageToken
import org.banana_inc.util.storage.tempStorage
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.seconds

@CommandAlias("test")
class Command: BaseCommand() {

    private val token = StorageToken("CMD-test-task", CancelableTask::class)

    @Default
    fun testCommand(player: Player, x: Float, y: Float) {
        val builder = ParticleBuilder(Particle.DUST).data(DustOptions(Color.GRAY, 1f)).count(10)
        Repeat.Async(duration = 10.seconds) {
            val offset = player.eyeLocation.relativeOffset(cos(it.getRadians()) * x, sin(it.getRadians()) * y, 1.0)
            builder.location(offset).receivers(offset.asyncNearbyPlayers(10)).spawn()
        }.apply {
            player.tempStorage["CMD-test-task"] = this.task
        }
    }

    @Subcommand("cancel")
    fun testCommand(player: Player) {
        player.sendMessage("${player.tempStorage}")
        player.tempStorage[token].cancel()
    }

    @Subcommand("registerChatEvent")
    fun registerTest(player: Player, args: String ) {
        player.sendMessage("registered event: $args")
        EventManager.addListener<AsyncChatEvent>{ it.player.sendMessage(args) }
    }
    @Subcommand("money")
    fun money(player: Player, amount: Long ) {
        player.data.money = amount
    }

    @Subcommand("savePlayer")
    fun saveMoney(player: Player ) {
        DatabaseActions.update(player.data)
    }

    @Subcommand("getData")
    fun getData(player: Player ) {
        player.sendMessage(player.data.toString())
    }

    @Subcommand("console")
    fun testConsole() {
        println("hello")
        val token = StorageToken("test", Int::class)
        3.tempStorage[token] = 7
        println((1 + 2).tempStorage[token])
    }

    @Subcommand("player")
    fun testConsole(player: Player) {
        println("hello")
        val token = StorageToken("test", Int::class)
        println("0: ${player.tempStorage}")
        player.tempStorage[token] = 7
        println("0.5: $player")
        println("1: ${player.tempStorage}")
        println((player).tempStorage[token])
    }


}
