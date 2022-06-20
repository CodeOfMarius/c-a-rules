package de.adesso.archunit.architecture.util

import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent

fun onlyHaveSimpleMethods() =
    object: ArchCondition<JavaMethod>("only be simple methods and not be domain logic") {
    var simpleMethods = listOf(
        Regex("component\\d*"),
        Regex("copy"),
        Regex("copy.default"),
        Regex("get\\w*"),
        Regex("set\\w*"),
        Regex("equals"),
        Regex("toString"),
        Regex("hashCode")
    )
    override fun check(item: JavaMethod, events: ConditionEvents) {
        if (simpleMethods.find { it.matches(item.name) } == null) {
            val message = "Method ${item.name} of class ${item.owner.fullName} suggest that this is not a simple method, allowed are only simple names"
            events.add(SimpleConditionEvent.violated(item.name, message))
        }
    }
}

fun haveOnlyPublicMethodsDeclaredInTheInterface() =
    object: ArchCondition<JavaMethod>("be part of the interface") {
        override fun check(item: JavaMethod, events: ConditionEvents) {
            val interfaceMethods = item.owner.interfaces.map {
                it.toErasure().methods
            }.flatMap { it.map { method -> method.name } }
            if (!interfaceMethods.contains(item.name)) {
                events.add(SimpleConditionEvent.violated(item.name, "Method ${item.name} of class ${item.owner.fullName} is not declared in any of the interfaces"))
            }
        }
    }

fun returnSomething() =
    object: ArchCondition<JavaMethod>("return something if they are inside a reading usecase") {
        override fun check(item: JavaMethod, events: ConditionEvents) {
            if (item.returnType.toErasure().name == "void") {
                events.add(SimpleConditionEvent.violated(item.name, "Method ${item.name} of class ${item.owner.fullName}  returns nothing (void), but is part of a query usecase"))
            }
        }
    }

fun notUseDomainObjectAsParameters(): ArchCondition<JavaMethod> =
    object : ArchCondition<JavaMethod>("not use domain objects as parameters") {
        val domain = ClassFileImporter().importPackages("de.adesso.archunit").filter {
            it.packageName.contains("domain")
        }.map { it.fullName }
        override fun check(item: JavaMethod, events: ConditionEvents) {
            for (type in item.parameterTypes) {
                if (domain.contains(type.name)) {
                    events.add(SimpleConditionEvent.violated(item, "${item.name} of class ${item.owner.fullName} uses the domain class ${type.name} as a parameter"))
                }
            }
        }
    }
fun notUseDomainObjectAsReturnValue(): ArchCondition<JavaMethod> =
    object : ArchCondition<JavaMethod>("not use domain objects as return value") {
        val domain = ClassFileImporter().importPackages("de.adesso.archunit").filter {
            it.packageName.contains("domain")
        }.map { it.fullName }
        override fun check(item: JavaMethod, events: ConditionEvents) {
                if (domain.contains(item.returnType.name)) {
                    events.add(SimpleConditionEvent.violated(item, "${item.name} of class ${item.owner.fullName} uses the domain class ${item.returnType.toErasure().name} as a return value"))
                }
            }
        }

