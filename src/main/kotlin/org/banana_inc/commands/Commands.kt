package org.banana_inc.commands

import org.bukkit.command.CommandExecutor

object Commands {
    var command1 = CommandExecutor { sender, _, _, _ ->
        sender.sendMessage("hi42")
        sender.sendMessage("12344")
        true
    }
}