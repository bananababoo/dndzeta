package org.banana_inc.chat

import net.kyori.adventure.extra.kotlin.plus
import org.banana_inc.extensions.clickableComponent
import org.banana_inc.extensions.component
import org.banana_inc.extensions.sendMessage
import org.banana_inc.logger
import org.banana_inc.onChat
import org.banana_inc.util.readable
import org.bukkit.entity.Player
import kotlin.reflect.KClass

open class MultiLineEditor<T : Any>(
    protected val player: Player,
    protected val type: KClass<T>,
    val lines: MutableList<T> = mutableListOf(),
    protected val onClose: MultiLineEditor<T>.() -> Unit,
){

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
        player.onChat(type){ newItem ->
             lines.add(newItem)
            renderEditor()
        }
    }

    protected val removeButton = { item: T ->
        "<red>[X]".clickableComponent {
             lines.remove(item)
            renderEditor()
        }
    }

    protected open val editButton = { item: T, index: Int ->
        "<gray> $index: <white> ".component.append(item.readable.clickableComponent {
            sendMessage(player,TYPE_NEW_MESSAGE)
            player.onChat(type) { newText: T ->
                lines.remove(item)
                lines.add(newText)
                renderEditor()
            }
        })
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

    init {
        renderEditor()
    }

}
