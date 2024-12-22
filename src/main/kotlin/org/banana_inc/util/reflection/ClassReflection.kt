package org.banana_inc.util.reflection

import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

object ClassReflection{
    fun getSuperClassesUpTo(clazz: KClass<*>, stopAt: KClass<*>): List<KClass<*>> {
        val superClasses = mutableListOf<KClass<*>>()
        var currentClass = clazz

        // Keep adding superclasses until we reach the stopping class
        while (currentClass != stopAt && currentClass != Any::class) {
            superClasses.add(currentClass)
            currentClass = currentClass.superclasses.firstOrNull() ?: break
        }

        return superClasses
    }
}