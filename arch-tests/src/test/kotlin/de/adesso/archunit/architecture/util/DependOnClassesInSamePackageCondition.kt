package de.adesso.archunit.architecture.util

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent

fun dependOnClassesInTheServicePackage() = object : ArchCondition<JavaClass>("be internal") {
    override fun check(item: JavaClass, events: ConditionEvents) {
        for (access in item.accessesFromSelf) {
            if (access.targetOwner != item && access.targetOwner.`package`.name.contains("service"))
                events.add(SimpleConditionEvent.violated(item, "${item.name} is in the same package"))
        }
    }
}

fun dependOnClassesInTheAdapterPackage(type: String) = object : ArchCondition<JavaClass>("be internal") {
    override fun check(item: JavaClass, events: ConditionEvents) {
        for (access in item.accessesFromSelf) {
            if (access.targetOwner != item && access.targetOwner.`package`.name.contains(type))
                events.add(SimpleConditionEvent.violated(item, "${item.name} is in the same package"))
        }
    }
}
