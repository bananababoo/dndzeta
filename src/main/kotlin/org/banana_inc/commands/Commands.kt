package org.banana_inc.commands

import com.zorbeytorunoglu.kLib.MCPlugin
import com.zorbeytorunoglu.kLib.task.Scopes
import com.zorbeytorunoglu.kLib.task.suspendFunctionSync
import kotlinx.coroutines.launch
import org.bukkit.command.CommandExecutor

class Commands(private val plugin: MCPlugin) {

    val command1 = CommandExecutor { sender, _, _, _ ->
        Scopes.supervisorScope.launch {
            for (i in 1..1000) {
                plugin.suspendFunctionSync {
                    sender.sendMessage(i.toString())
                }
            }
        }
        true
    }
}