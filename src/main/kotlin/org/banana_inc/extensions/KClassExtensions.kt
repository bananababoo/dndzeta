package org.banana_inc.extensions

import kotlin.reflect.KClass

val KClass<*>.getFirstClassTypeArgument: KClass<*>
    get() = this.supertypes.first().arguments.first().type!!.classifier as KClass<*>

fun KClass<*>.getClassTypeArgument(typeIndex: Int): KClass<*> {
    return this.supertypes.first().arguments[typeIndex].type!!.classifier as KClass<*>
}
