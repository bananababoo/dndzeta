package org.banana_inc.gui.base

import org.banana_inc.data.PlayerData

abstract class GUI(val playerData: PlayerData) {

    init {
        playerData.localData.inGUI = true
    }

    fun onClose(){
        playerData.localData.inGUI = false
    }

}