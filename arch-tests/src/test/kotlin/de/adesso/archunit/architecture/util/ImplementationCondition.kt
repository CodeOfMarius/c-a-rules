package de.adesso.archunit.architecture.util

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent

fun internalInAdapterShouldImplementWithSameName() = object : ArchCondition<JavaClass>(" validates naming convention") {
    override fun check(item: JavaClass, events: ConditionEvents) {
        if (!item.simpleName.startsWith("Internal")) {
            events.add(SimpleConditionEvent.violated(item, "${item.fullName} does not have the prefix internal"))
        }
        if (!item.simpleName.endsWith("Adapter")) {
            events.add(SimpleConditionEvent.violated(item, "${item.fullName} does not have the suffix adapter"))
        }
        val name = item.simpleName.substringAfter("Internal").substringBefore("Adapter")
        if (!item.reflect().interfaces.map { it.simpleName }.contains("Internal${name}Port")) {
            events.add(SimpleConditionEvent.violated(item, "${item.name} should implement the corresponding port"))
        }
    }
}

fun internalOutAdapterShouldImplementWithSameName() = object : ArchCondition<JavaClass>(" validates naming convention") {
    override fun check(item: JavaClass, events: ConditionEvents) {
        if (!item.simpleName.startsWith("Internal")) {
            events.add(SimpleConditionEvent.violated(item, "${item.fullName} does not have the prefix internal"))
        }
        if (!item.simpleName.endsWith("Adapter")) {
            events.add(SimpleConditionEvent.violated(item, "${item.fullName} does not have the suffix adapter"))
        }
        val name = item.simpleName.substringAfter("Internal").substringBefore("Adapter")
        if (item.reflect().interfaces.size == 1 && !item.reflect().interfaces.map { it.simpleName }.contains("${name}Port")) {
            events.add(SimpleConditionEvent.violated(item, "${item.fullName} should implement the corresponding port"))
        }
    }
}

fun adapterShouldImplementWithSameName() = object : ArchCondition<JavaClass>(" validates naming convention") {
    override fun check(item: JavaClass, events: ConditionEvents) {
        if (!item.simpleName.endsWith("Adapter")) {
            events.add(SimpleConditionEvent.violated(item, "${item.fullName} does not have the suffix adapter"))
        }
        val name = item.simpleName.substringBefore("Adapter")
        if (item.reflect().interfaces.size == 1 && !item.reflect().interfaces.map { it.simpleName }.contains("${name}Port")) {
            events.add(SimpleConditionEvent.violated(item, "${item.fullName} should implement the corresponding port"))
        }
    }
}

fun haveOnlyOneInterface() =
    object: ArchCondition<JavaClass>("should have exactly one interface") {
        override fun check(item: JavaClass, events: ConditionEvents) {
            val interfaces = item.interfaces.size;
            if (interfaces >= 2) {
                events.add(SimpleConditionEvent.violated(item.name, "${item.name} has too many interfaces"))
            }
            if (interfaces < 1) {
                events.add(SimpleConditionEvent.violated(item.name, "${item.name} does not implement an interface"))
            }
        }
    }
