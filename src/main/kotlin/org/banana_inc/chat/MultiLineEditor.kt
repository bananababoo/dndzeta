package org.banana_inc.chat

import net.kyori.adventure.extra.kotlin.plus
import org.banana_inc.EventManager.chatCallback
import org.banana_inc.extensions.clickableComponent
import org.banana_inc.extensions.sendMessage
import org.banana_inc.logger
import org.bukkit.entity.Player
import kotlin.reflect.KClass

open class MultiLineEditor<T : Any>(
    protected val player: Player,
    protected val type: KClass<T>,
    protected val list: MutableCollection<T> = mutableListOf(),
    protected val onClose: MultiLineEditor<T>.() -> Unit
){
    val lines: List<T>
        get() = list.toList()

    companion object {
        protected const val BORDER = "<gray>-------"
        private const val TYPE_NEW_MESSAGE = "<gold>Type a new message"
    }

    private val exitButton = "<light_purple>[Exit]".clickableComponent {
        onClose()
    }

    protected open val addButton
        get() = "<green>[+]".clickableComponent {
        sendMessage(player,TYPE_NEW_MESSAGE)
        player.chatCallback(type){ newItem ->
             list.add(newItem)
            renderEditor()
        }
    }

    protected val removeButton = { item: T ->
        "<red>[X]".clickableComponent {
             list.remove(item)
            renderEditor()
        }
    }

    protected open val editButton = { item: T, index: Int ->
        "<gray> $index: <white>${item}".clickableComponent {
            sendMessage(player,TYPE_NEW_MESSAGE)
            player.chatCallback(type) { newText: T ->
                list.remove(item)
                list.add(newText)
                renderEditor()
            }
        }
    }

    fun open(){
        renderEditor()
    }

    protected open fun sendEditorContentLine(index: Int, item: T){
        sendMessage(player,removeButton(item) + editButton(item,index))
    }

    protected fun renderEditor() {
        sendMessage(player,BORDER)
        lines.forEachIndexed { index, item ->
            sendEditorContentLine(index, item)
        }
        logger.info(addButton.toString())
        sendMessage(player,addButton)
        sendMessage(player,exitButton)
        sendMessage(player,BORDER)
    }

}
