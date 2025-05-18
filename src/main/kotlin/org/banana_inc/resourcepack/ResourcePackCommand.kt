package org.banana_inc.resourcepack

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.banana_inc.logger

@CommandAlias("resourcepack|rp")

object ResourcePackCommand: BaseCommand(){

    @Subcommand("reload")
    @CommandPermission("zeta.resourcepack.reload")
    fun reload() {
        logger.info("Reloading resource pack")
        ResourcePackProvider.reloadResourcePackAsync()
    }

}