package org.banana_inc.util

fun hash(input: Int): Int {
    var x = ((input shr  16) xor input) * 0x45d9f3b
    x = ((x shr 16) xor x) * 0x45d9f3b
    x = (x shr 16) xor x
    return x
}