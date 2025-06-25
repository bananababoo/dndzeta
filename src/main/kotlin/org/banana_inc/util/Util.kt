package org.banana_inc.util

fun loop(repetitions: Int, action: (backToStart: () -> Unit, index: Int) -> Unit, finally: () -> Unit = {}) {
    lateinit var looper: () -> Unit
    var index = 0
    looper = loopStart@{
        index += 1
        if(index == repetitions) return@loopStart finally()
        action(looper, index)
    }
    action(looper, index)
}