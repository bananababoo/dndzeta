package org.banana_inc.data


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DatabaseUpdateHandler(val propertyName: String)
