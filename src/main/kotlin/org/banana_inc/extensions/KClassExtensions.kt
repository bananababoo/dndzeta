package org.banana_inc.extensions

import kotlin.reflect.KClass

val KClass<*>.getFirstTypeArgumentClass: KClass<*>
        get() = this.supertypes.first().arguments.first().type!!.classifier as KClass<*>
