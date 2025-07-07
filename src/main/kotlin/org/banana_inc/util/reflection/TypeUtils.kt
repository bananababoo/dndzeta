package org.banana_inc.util.reflection

import kotlin.reflect.KClass

fun <T: Any> makeMutableTypedList(type: KClass<T>): MutableList<T> = mutableListOf()
