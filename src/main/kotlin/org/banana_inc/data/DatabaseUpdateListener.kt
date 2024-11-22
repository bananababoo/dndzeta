package org.banana_inc.data

/**
 * Methods with this annotation are required to have 0 arguments
 * and will be called when the property changes with the new changes already applied
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DatabaseUpdateListener(val propertyName: String, val includeOldData: Boolean = false)