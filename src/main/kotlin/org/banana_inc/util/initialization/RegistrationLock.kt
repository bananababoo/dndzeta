package org.banana_inc.util.initialization

/**
 * Prevents multiple registrations. Used
 * by functions that register data once ever,
 * on initialization.
 */
object RegistrationLock {
    private val locks: MutableList<Any> = arrayListOf()

    fun register(obj: Any){
        check(obj !in locks)
        locks.add(obj)
    }

    fun isRegistered(obj: Any): Boolean{
        return obj !in locks
    }

}