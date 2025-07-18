package org.banana_inc.extensions

import com.fasterxml.jackson.module.kotlin.readValue
import net.kyori.adventure.text.Component
import org.banana_inc.data.Database
import org.banana_inc.item.Item
import org.banana_inc.item.ItemStackCreator
import org.banana_inc.item.Modifier
import org.banana_inc.item.items.ItemData
import org.banana_inc.item.items.Weapon.Melee.Simple.Handaxe.create
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

/**
 * Gets the display name of the item or sets one.
 * @return Name as string.
 */
var ItemMeta.name: Component?
    get() = if (hasDisplayName()) displayName() else null
    set(value) { displayName(value ?: Component.empty()) }

fun ItemStack.changeMeta(action: (ItemMeta).() -> Unit): ItemStack {
    val meta = (if (this.hasItemMeta()) this.itemMeta else Bukkit.getItemFactory().getItemMeta(this.type))!!
    action(meta)
    itemMeta = meta
    return this
}

fun <T> ItemStack.useMeta(action: (ItemMeta).() -> T): T {
    val meta = if (this.hasItemMeta()) this.itemMeta else Bukkit.getItemFactory().getItemMeta(this.type)
    val result: T = action(meta)
    itemMeta = meta
    return result
}

/**
 * Removes specific amount of item from the item stack of player, updates the inventory of the player.
 * @param player Player
 * @param amount Amount to be removed
 */
fun ItemStack.removeAmount(player: Player, amount: Int) {

    if (this.amount - amount <= 0) {
        if (player.inventory.itemInMainHand == this) {
            player.inventory.itemInMainHand.amount = 0
        } else {
            player.inventory.removeItem(this)
        }
        return
    }

    this.amount -= amount
    player.updateInventory()

}

/**
 * Sets the lore of the item stack from string vararg and colors them.
 * @param lore String vararg
 */
fun ItemStack.setLore(vararg lore: String) {

    val meta = (if (this.hasItemMeta()) this.itemMeta else Bukkit.getItemFactory().getItemMeta(this.type))

    val colored = ArrayList<Component>()
    lore.forEach { colored.add(it.component) }
    meta.lore(colored)

    this.itemMeta = meta

}

/**
 * Sets the lore of the item stack from string list.
 * @param loreList List<String>
 */
fun ItemStack.setLore(loreList: List<Component>) {

    val meta = (if (this.hasItemMeta()) this.itemMeta else Bukkit.getItemFactory().getItemMeta(this.type))

    meta.lore(loreList)

    this.itemMeta = meta

}

/**
 * Adds the string vararg to the current lore.
 * @param lore String vararg
 */
fun ItemStack.addLore(vararg lore: Component?) {

    val meta = (if (this.hasItemMeta()) this.itemMeta else Bukkit.getItemFactory().getItemMeta(this.type))!!
    var loreList = meta.lore()

    if (loreList == null) {
        loreList = lore.asList()
    } else {
        loreList.addAll(lore.asList())
    }
    meta.lore(loreList)
    this.itemMeta = meta

}

/**
 * Adds the string list to the current lore.
 * @param lore String list
 */
fun ItemStack.addLore(lore: List<Component>) {

    val meta = (if (this.hasItemMeta()) this.itemMeta else Bukkit.getItemFactory().getItemMeta(this.type))!!
    var loreList = meta.lore()

    if (loreList == null) loreList = lore() else lore()?.let { loreList.addAll(lore) }

    meta.lore(loreList)
    this.itemMeta = meta

}

/**
 * Sets an enchantment for the item stack with level.
 * @param enchantment Enchantment
 * @param level Level of the enchantment
 */
fun ItemStack.setEnchantment(enchantment: Enchantment, level: Int) {
    val meta = (if (this.hasItemMeta()) this.itemMeta else Bukkit.getItemFactory().getItemMeta(this.type))!!
    meta.addEnchant(enchantment, level, true)
    this.itemMeta = meta
}

/**
 * Clears all the enchantments from the item stack.
 */
fun ItemStack.clearEnchantments() {
    val meta = (if (this.hasItemMeta()) this.itemMeta else Bukkit.getItemFactory().getItemMeta(this.type))!!
    for (enchantment in meta.enchants.keys) {
        meta.removeEnchant(enchantment)
    }
    this.itemMeta = meta
}


/**
 * Checks if the given material is a pickaxe.
 * @return True if it is a pickaxe, false otherwise.
 */
val Material.isPickaxe: Boolean get() = name.endsWith("_PICKAXE")
/**
 * Checks if the given material is a sword.
 * @return True if it is a sword, false otherwise.
 */
val Material.isSword: Boolean get() = name.endsWith("_SWORD")
/**
 * Checks if the given material is an axe.
 * @return True if it is an axe, false otherwise.
 */
val Material.isAxe: Boolean get() = name.endsWith("_AXE")
/**
 * Checks if the given material is a spade.
 * @return True if it is a spade, false otherwise.
 */
val Material.isSpade: Boolean get() = name.endsWith("_SPADE")
/**
 * Checks if the given material is a hoe.
 * @return True if it is a hoe, false otherwise.
 */
val Material.isHoe: Boolean get() = name.endsWith("_HOE")
/**
 * Checks if the given material is a helmet.
 * @return True if it is a helmet, false otherwise.
 */
val Material.isHelmet: Boolean get() = name.endsWith("_HELMET")
/**
 * Checks if the given material is a chest plate.
 * @return True if it is a chest plate, false otherwise.
 */
val Material.isChestPlate: Boolean get() = name.endsWith("_CHESTPLATE")
/**
 * Checks if the given material is a leggings.
 * @return True if it is a leggings, false otherwise.
 */
val Material.isLeggings: Boolean get() = name.endsWith("_LEGGINGS")
/**
 * Checks if the given material is boots.
 * @return True if it is boots, false otherwise.
 */
val Material.isBoots: Boolean get() = name.endsWith("_BOOTS")
/**
 * Checks if the given material is an ore.
 * @return True if it is an ore, false otherwise.
 */
val Material.isOre: Boolean get() = name.endsWith("_ORE")
/**
 * Checks if the given material is an ingot.
 * @return True if it is an ingot, false otherwise.
 */
val Material.isIngot: Boolean get() = name.endsWith("_INGOT")
/**
 * Checks if the given material is a door.
 * @return True if it is a door, false otherwise.
 */
val Material.isDoor: Boolean get() = name.endsWith("_DOOR")
/**
 * Checks if the given material is a mine cart.
 * @return True if it is a mine cart, false otherwise.
 */
val Material.isMineCart: Boolean get() = name.endsWith("_MINECART")

@OptIn(ExperimentalStdlibApi::class)
val ItemStack.toItem: Item<*>
    @Throws(IllegalStateException::class)
    get() {
        check(type != Material.AIR) { "Air isn't an item" }
        checkNotNull(itemMeta) { "Item: $type has null item meta" }
        return useMeta {
            val id = persistentDataContainer[ItemStackCreator.idKey, PersistentDataType.STRING] ?: error("no id")
            val modifierBytes = persistentDataContainer[ItemStackCreator.modifiersKey, PersistentDataType.STRING]
            val modifiers = if(modifierBytes != null) Database.objectMapper.readValue<MutableSet<out Modifier<*>>>(modifierBytes) else mutableSetOf()
            ItemData[id]!!.create(modifiers=modifiers).apply {
            amount = this@toItem.amount
        }
    }
}
