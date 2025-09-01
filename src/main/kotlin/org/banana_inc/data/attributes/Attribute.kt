package org.banana_inc.data.attributes

open class Attribute<T>(val modifiers: HashMap<String, T> = HashMap()) {

    @Suppress("UNCHECKED_CAST")
    open fun get(): T {
        val iterator = modifiers.values.iterator()
        var value = iterator.next()

        if(value is Set<*>){
            return (modifiers.values as Collection<Set<*>>).flatten() as T
        }

        while(iterator.hasNext()) {
            val next = iterator.next()
            value = when(next){
                is  Int -> (value as Int + next) as T
                is Boolean -> (value as Boolean || next) as T
                else -> error("Tried to merge $value, in attributes which isn't supported")
            }
        }
        return value
    }

    operator fun set(instance: String, amount: T){
        modifiers[instance] = amount
    }

    fun putIfAbsent(instance: String, amount: T){
        modifiers.putIfAbsent(instance, amount)
    }

    override fun toString() = modifiers.toString()

    companion object{
        inline fun <reified T> merge(a: T, b: T): T{
            return when(a){
                is Int -> (a + (b as Int)) as T
                is Boolean -> (a || (b as Boolean)) as T
                else -> error("Tried to merge ${T::class}, in attributes which isn't supported")
            }
        }
    }

}