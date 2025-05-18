package org.banana_inc.gui.base

import org.banana_inc.data.Data

abstract class GUI(val player: Data.Player) {

    init {
        player.localData.inGUI = true
    }

    fun onClose(){
        player.localData.inGUI = false
    }

}