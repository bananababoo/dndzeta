package org.banana_inc.chat

import net.kyori.adventure.extra.kotlin.plus
import org.banana_inc.EventManager
import org.banana_inc.extensions.clickableComponent
import org.banana_inc.extensions.sendMessage
import org.bukkit.entity.Player

class MultiLineEditor(
    private val player: Player,
    private val list: MutableCollection<String> = mutableListOf(),
    private val onClose: MultiLineEditor.() -> Unit
){

    val lines: List<String>
        get() = list.toList()

    init {
        renderEditor()
    }

    private  val border = "<gray>-------"
    private val typeNewMessage = "<gold>Type a new message..."
    private val exitButton = "<light_purple>[Exit]".clickableComponent {
        onClose()
    }
    private val addButton = "<green>[+]".clickableComponent {
        sendMessage(player,typeNewMessage)
        EventManager.chatCallback { newItem: String ->
             list.add(newItem)
            renderEditor()
        }
    }
    private val removeButton = {item: String ->
        "<red>[X]".clickableComponent {
             list.remove(item)
            renderEditor()
        }
    }
    private val editButton = {  item: String, index: Int ->
        "<gray> $index: <white>${item}".clickableComponent {
            sendMessage(player,typeNewMessage)
            EventManager.chatCallback { newText: String ->
                list.remove(item)
                list.add(newText)
                renderEditor()
            }
        }
    }

    private fun renderEditor() {
        sendMessage(player,border)
        lines.forEachIndexed { index, item ->
            sendMessage(player,removeButton(item) + editButton(item,index))
        }
        sendMessage(player,addButton)
        sendMessage(player,exitButton)
        sendMessage(player,border)
    }

}
