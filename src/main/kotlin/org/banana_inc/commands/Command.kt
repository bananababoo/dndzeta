@file:Suppress("unused")

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
import org.banana_inc.chat.MultiLineEditor
import org.banana_inc.data.DatabaseActions
import org.banana_inc.extensions.*
import org.banana_inc.item.EnchantmentType
import org.banana_inc.item.ItemData
import org.banana_inc.item.ItemData.Weapon.Melee.Martial.Halberd.create
import org.banana_inc.item.Modifier
import org.banana_inc.logger
import org.banana_inc.util.ContextResolver
import org.banana_inc.util.storage.StorageToken
import org.banana_inc.util.storage.tempStorage
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.seconds


@CommandAlias("test")
object Command: BaseCommand() {

    private val token = StorageToken<CancelableTask>("CMD-test-task")

    @Default
    fun testCommand(player: Player, x: Float, y: Float) {
        val builder = ParticleBuilder(Particle.DUST).data(DustOptions(Color.GRAY, 1f)).count(10)
        Repeat.Async(duration = 10.seconds) {
            val offset = player.eyeLocation.relativeOffset(cos(it.getRadians()) * x, sin(it.getRadians()) * y, 1.0)
            builder.location(offset).radiusAsyncCached(10,20).spawn()
        }.apply {
            player.tempStorage["CMD-test-task"] = this.task
        }
    }

    @Subcommand("cancel")
    fun testCommand(player: Player) {
        player.sendMessage("${player.tempStorage}")
        player.tempStorage[token]!!.cancel()
    }

    @Subcommand("registerChatEvent")
    fun registerTest(player: Player, args: String ) {
        player.sendMessage("registered event: $args")
        EventManager.addListener<AsyncChatEvent>{  player.sendMessage(args) }
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

        val token = StorageToken<Int>("test")
        3.tempStorage[token] = 7
        println((1 + 2).tempStorage[token]) //prints 7

        "hello".tempStorage["hi"] = 14
        ContextResolver.resolve<Double>("1592")
    }

    @Subcommand("test")
    fun testConsole(input: String) {
        logger.info(input.resolve<Material>().toString())
    }

    @Subcommand("player")
    fun testConsole(player: Player) {
        println("hello")
        val token = StorageToken<Int>("test")
        println("0: ${player.tempStorage}")
        player.tempStorage[token] = 7
        println("0.5: $player")
        println("1: ${player.tempStorage}")
        println((player).tempStorage[token])
    }

    @Subcommand("beep")
    fun bop(p: Player){
        val gun = ItemData.Weapon.Ranged.Martial.Gun.Blowgun.create()
        gun.modifiers.add(Modifier.Enchantment(EnchantmentType.SPEEDY, 1))
        p.data.inventory[0] = gun
        DatabaseActions.updateThenAsync(p.data) {
            val i = p.data.inventory[0] ?: error("item not found")
            sendMessage(p,"id: ${i.type.name}")
            if (i.type is ItemData.Weapon.Ranged)
                sendMessage(p, i.type.damageDice.toString())

        }
    }

    @Subcommand("editor")
    fun editor(player: Player){
        MultiLineEditor(player) {
            player.sendMessage("finished: $lines")
        }
    }

    @Subcommand("inventory")
    fun inventory(player: Player){
        player.sendMessage(player.data.inventory.toString())
    }

}
