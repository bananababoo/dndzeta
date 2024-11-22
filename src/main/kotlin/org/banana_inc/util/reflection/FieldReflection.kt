package org.banana_inc.util.reflection

import org.bson.Document
import java.lang.reflect.Field

object FieldReflection {
    fun getDifferences(obj1: Any?, obj2: Any?, path: String = ""): List<String> {
        val differences = mutableListOf<String>()

        // Handle null cases
        if (obj1 == null && obj2 == null) return differences
        if (obj1 == null || obj2 == null) {
            differences.add(path)
            return differences
        }

        // Get all fields of both objects (including private fields)
        val fields1 = obj1.javaClass.declaredFields
        val fields2 = obj2.javaClass.declaredFields

        val allFields = fields1.toSet() union fields2.toSet() // Combine fields from both objects

        for (field in allFields) {
            field.isAccessible = true // Make private fields accessible
            val value1 = getFieldValue(obj1, field)
            val value2 = getFieldValue(obj2, field)

            // Construct the full path to the field (e.g., "address.city")
            val fullPath = if (path.isEmpty()) field.name else "$path.${field.name}"

            when {
                value1 is Document && value2 is Document -> {
                    // Recurse for nested Documents
                    differences.addAll(getDifferences(value1, value2, fullPath))
                }
                value1 != value2 -> {
                    // Add difference for scalar or mismatched types
                    differences.add(fullPath)
                }
            }
        }

        return differences
    }

    private fun getFieldValue(obj: Any, field: Field): Any? {
        return try {
            field.get(obj) // Get the value of the field
        } catch (e: IllegalAccessException) {
            null // In case the field is not accessible
        }
    }
}