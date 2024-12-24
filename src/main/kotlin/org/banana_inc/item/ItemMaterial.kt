package org.banana_inc.item

import org.bukkit.Material

enum class ItemMaterial(val material: Material) {
    GUN(Material.NETHERITE_HOE),
    CROSSBOW(Material.CROSSBOW),
    BOW(Material.BOW),
    WEAPON(Material.NETHERITE_SWORD),
    THROWABLE(Material.SNOWBALL),
    AMMO(Material.ARROW),
    ITEM(Material.NETHERITE_INGOT)
}