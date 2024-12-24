package org.banana_inc.gui

import org.banana_inc.data.Data

open class GUI(val player: Data.Player) {
    init {
        player.localData.inGUI = true
    }

    fun onClose(){
        player.localData.inGUI = false
    }

}