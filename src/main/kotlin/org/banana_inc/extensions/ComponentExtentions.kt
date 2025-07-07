package org.banana_inc.extensions

import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.jetbrains.annotations.NotNull

val Component.minimessage: String
    get() = MiniMessage.miniMessage().serialize(this)

operator fun Component.plus(string: String): Component{
    return this.plus(string.component)
}

fun Component.clickableComponent(onClick: () -> Unit): @NotNull Component {
    val a = this.clickEvent(ClickEvent.callback { onClick() })
    return a
}
