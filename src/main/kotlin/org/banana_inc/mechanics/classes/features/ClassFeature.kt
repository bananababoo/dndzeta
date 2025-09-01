package org.banana_inc.mechanics.classes.features

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.banana_inc.data.attributes.AttributeModifier

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
abstract class ClassFeature: AttributeModifier {

}