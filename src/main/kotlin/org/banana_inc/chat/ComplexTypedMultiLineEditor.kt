package org.banana_inc.chat

import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.text.Component
import org.banana_inc.EventManager.chatCallback
import org.banana_inc.extensions.clickableComponent
import org.banana_inc.extensions.component
import org.banana_inc.extensions.sendMessage
import org.banana_inc.logger
import org.banana_inc.util.loop
import org.bukkit.entity.Player
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class ComplexTypedMultiLineEditor<T : Any>(
    player: Player,
    type: KClass<T>,
    onClose: MultiLineEditor<T>.() -> Unit):
    MultiLineEditor<T>(player,type,onClose=onClose) {

    override val addButton
        get() = "<green>[+]".clickableComponent {
            val params: MutableList<Any> = mutableListOf()
            loop(attributes.size,{ backToStart, index ->
                val attribute = attributes.entries.elementAt(index)
                sendMessage(player, "Select a ${attribute.value} of type ${attribute.key.simpleName}")
                player.chatCallback(attribute.key){
                    params.add(it)
                    backToStart()
                }
            }) {
                list.add(type.primaryConstructor!!.call(*params.toTypedArray()))
                renderEditor()
            }
        }

    val attributeEditButton = { item: T, index: Int, attribute: Map.Entry<KClass<*>, String> ->
        val property = type.java.getDeclaredField(attribute.value)
        property.trySetAccessible()
        "${property.name}: ${property.get(item)} ".clickableComponent {
            sendMessage(player, "Select a ${attribute.value} of type ${attribute.key.simpleName}")
            player.chatCallback(attribute.key) {
                property.set(item, it)
                renderEditor()
            }
        }
    }
    
    override fun sendEditorContentLine(index: Int, item: T){
        var editButton: Component = Component.empty()
        for (attribute in attributes) {
            editButton += attributeEditButton(item,index,attribute)
        }
        sendMessage(player,removeButton(item) + "<white> $index: ".component + editButton)
    }

    private val attributes: LinkedHashMap<KClass<*>, String> by lazy { //preserves order
        val map = LinkedHashMap<KClass<*>, String>()
        for (parameter in type.primaryConstructor!!.parameters) {
            logger.info("adding ${parameter.type.classifier as KClass<*>} to ${parameter.name!!}")
            map.put(parameter.type.classifier as KClass<*>, parameter.name!!)
        }
        map
    }

}