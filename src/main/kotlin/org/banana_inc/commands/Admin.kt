package org.banana_inc.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.banana_inc.extensions.syncInventoryToData
import org.bukkit.entity.Player

@CommandAlias("clear") object Clear: BaseCommand(){ @Default fun clear(p: Player){ p.inventory.clear(); p.inventory.syncInventoryToData() } }