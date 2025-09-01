package org.banana_inc.visuals.resourcepack

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.banana_inc.config.ServerConfig
import org.banana_inc.config.config
import org.banana_inc.extensions.data
import org.banana_inc.gui.base.GUI
import org.banana_inc.gui.base.item.ModifiableItem
import org.banana_inc.gui.button.processors.ToggleItem
import org.banana_inc.util.initialization.InitOnStartup
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.invui.Click
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.Structure
import xyz.xenondevs.invui.item.ItemBuilder
import xyz.xenondevs.invui.window.Window

@CommandAlias("resourcepackselector|rpsl")
object ResourcePackSelector: BaseCommand() {
    @Default
    fun openSelector(player: Player){
        ResourcePackSelection(player)
    }
}

class ResourcePackSelection(player: Player): GUI(player.data) {
    @InitOnStartup
    companion object {
        init {
            Structure.addGlobalIngredient('#', ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(""))
        }

        val border = ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
    }

    private val gui = Gui.builder()
        .setStructure(
            "# # # C M # # # #" // left add filter search
        )
        .addIngredient('M', ToggleResourcePack(config.resource_packs.music, Material.JUKEBOX))
        .addIngredient('C', ToggleResourcePack(config.resource_packs.default, Material.GRASS_BLOCK))
        .build()

    private var window = Window.builder().apply {
        setViewer(player)
        setTitle("InvUI")
        setUpperGui(gui)
    }

    init {
        window.open(player)
    }

    class ToggleResourcePack(val type: ServerConfig.ResourcePackConfig.Data, material: Material) : ModifiableItem(
        ItemStack(material)
    ){
        private val toggleState = ToggleItem(true)
        init {
            addProcessor(toggleState)
        }

        override fun onClick(clickType: ClickType, player: Player, click: Click) {
            if (toggleState.enabled) {
                if(type.primary){
                    player.data.settings.resourcePackOptions.removeAll{ it.primary }
                }
                ResourcePackProvider.applyResourcePack(player, type)
            } else {
                if(type.primary) return
                ResourcePackProvider.removeResourcePack(player, type)
            }
        }
    }
}