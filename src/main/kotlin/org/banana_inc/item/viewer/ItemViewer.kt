package org.banana_inc.item.viewer

import org.banana_inc.extensions.addItem
import org.banana_inc.extensions.component
import org.banana_inc.extensions.data
import org.banana_inc.extensions.useMeta
import org.banana_inc.gui.base.GUI
import org.banana_inc.gui.button.generic.ChangePageItem
import org.banana_inc.gui.button.generic.ClickToGetItem
import org.banana_inc.item.Item
import org.banana_inc.item.data.ItemData
import org.banana_inc.item.data.Weapon.Melee.Simple.Handaxe.create
import org.banana_inc.util.initialization.InitOnStartup
import org.banana_inc.util.reflection.ClassReflection
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.invui.Click
import xyz.xenondevs.invui.gui.*
import xyz.xenondevs.invui.item.ItemBuilder
import xyz.xenondevs.invui.window.AnvilWindow

class ItemViewer(player: Player): GUI(player.data) {
    @InitOnStartup
    companion object {
        init{
            Structure.addGlobalIngredient('#', ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(""))
        }
        val border = ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
    }

    private val allItems: List<ItemViewerItem> = ItemData.sortedItemClasses.map {
            ItemViewerItem(ItemData[it].create(), this)
    }.toList()

    private val gui = PagedGui.itemsBuilder()
        .setStructure(
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . o . . . .",
            "# s s s s s s s #" // left add filter search
        )
        .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
        .addIngredient('o', ChangePageItem())
        .addIngredient('s', ItemStack.empty())
        .setContent(allItems)
        .build()

    private var query = ""
    private var inStorage = mutableListOf<ItemViewerItem>()

    private fun updateItems(player: Player? = null){
        val items = if(query == "") allItems
            else if(query.startsWith("#")) filterType()
            else allItems.filter {
                    it.item.type.name.contains(query, ignoreCase = true)
                }
        gui.content = items
        gui.apply {
            (1..7).forEach { index ->
                gui[27 + index] = xyz.xenondevs.invui.item.Item.simple (
                    inStorage.getOrNull(index - 1)?.item?.itemStack() ?: ItemStack.empty()
                )
            }
        }
        window.setLowerGui(gui)
        if(player != null) window.open(player)
    }

    private fun filterType(): List<ClickToGetItem>{
        val filters = query.substring(1).split(",")
        return allItems.filter { item ->
            val superClasses = ClassReflection.getSuperClassesUpTo(item.item.type::class, ItemData::class)
            filters.all{ filter ->
                superClasses.any{ it.simpleName!!.contains(filter, ignoreCase = true) }
            }
        }
    }

    private var window = AnvilWindow.builder().apply {

        setViewer(player)
        setTitle("InvUI")
        setUpperGui(Gui.builder()
            .setStructure("a a a")
            .addIngredient('a', ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .apply {
                    useMeta {
                        displayName("".component)
                    }
                }
            )
        )
        setLowerGui(gui)
        addRenameHandler {
            query = it
            updateItems()
        }
        addOpenHandler { updateItems() }
        addCloseHandler{
            onClose()
            player.closeInventory()
            player.inventory.addItem(*inStorage.map{ it.item }.toTypedArray())
        }
    }

    init {
        window.open(player)
    }

    class ItemViewerItem(item: Item<*>, private val viewer: ItemViewer): ClickToGetItem(item){
        override fun handleClick(p0: ClickType, player: Player, p2: Click) {
            val storageItem = viewer.inStorage.find { id -> id.item.type == item.type }
            when(p0){
                ClickType.LEFT -> storageItem?.let { it.item.amount += 1 } ?: viewer.inStorage.add(ItemViewerItem(item.copy(),viewer).apply { viewer.updateItems() })
                ClickType.SHIFT_LEFT -> storageItem?.let { it.item.amount += 64 } ?: viewer.inStorage.add(ItemViewerItem(item.copy().apply { amount = 64 },viewer).apply { viewer.updateItems() })
                ClickType.RIGHT -> if(storageItem != null) if(storageItem.item.amount == 1) { storageItem.viewer.updateItems(); viewer.inStorage.remove(storageItem) } else storageItem.item.amount -= 1
                ClickType.SHIFT_RIGHT -> {
                    if(storageItem != null) {
                        viewer.inStorage.remove(storageItem)
                        storageItem.viewer.updateItems()
                    }
                }
                else -> return
            }
            viewer.updateItems()
        }
    }

}