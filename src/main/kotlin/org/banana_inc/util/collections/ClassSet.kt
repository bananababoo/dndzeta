package org.banana_inc.util.collections

import kotlin.reflect.KClass

/**
 * Set of objects subclassed of abstract class/interface T.
 * No 2 objects in this set will have the same subclassed class of T
 */
class ClassSet<T : Any>(existingSet: ClassSet<T>?=null): LinkedHashSet<T>() {
    init {
        if(existingSet!= null){
            addAll(existingSet)
        }
    }

    val classSet: MutableSet<KClass<out T>> = mutableSetOf<KClass<out T>>()

    override fun add(e: T): Boolean {
        removeClass(e::class)
        return super.add(e)
    }

    override fun addLast(e: T) {
        removeClass(e::class)
        super.addLast(e)
    }

    override fun addFirst(e: T) {
        removeClass(e::class)
        super.addFirst(e)
    }

    override fun addAll(c: Collection<T>): Boolean {
        c.forEach { removeClass(it::class) }
        return super.addAll(c)
    }

    fun removeClass(existing: KClass<out T>){
        if(classSet.contains(existing)){
            super.removeIf { it::class == existing }
            classSet.remove(existing)
        }
        classSet.add(existing)
    }
    fun copy(): ClassSet<T> {
        return ClassSet(this)
    }

}