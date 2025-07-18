package org.banana_inc.extensions


inline fun <reified T : Enum<T>> enumValueOf(value: String, ignoreCase: Boolean): Enum<T> {
    return enumValues<T>().first { it.name.equals(value, ignoreCase) }
}

fun Enum<*>.readableName(): String {
    return name.capitalizeFirstLetter()
}


