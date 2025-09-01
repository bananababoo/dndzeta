package org.banana_inc.mechanics.classes.action

import org.banana_inc.data.PlayerData


data class Action(
    val action: (PlayerData) -> (Unit),
    val displayCondition: (PlayerData) -> (Boolean)){
    constructor(action: (PlayerData) -> (Unit)): this(action, { (_) -> true})
}