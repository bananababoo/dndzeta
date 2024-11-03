package org.banana_inc.extensions

import org.bukkit.event.player.PlayerMoveEvent

/**
 * Checks if the player displaced from its previous location.
 * @return True if yes, false if no.
 */
val PlayerMoveEvent.displaced: Boolean
    get() = this.from.x != this.to.x || this.from.y != this.to.y || this.from.z != this.to.z