@file:JvmName("InternalPredicates")
package de.adesso.archunit.architecture.util

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import kotlin.reflect.KVisibility

fun isKotlinInternal() = object : DescribedPredicate<JavaClass>("Kotlin internal class") {
    override fun apply(input: JavaClass) = input.reflect().isKotlinInternal()
    private fun Class<*>.isKotlinInternal() = isKotlinClass() && isInternal()
}

fun isKotlinNotInternal() = object : DescribedPredicate<JavaClass>("Kotlin not-internal class") {
    override fun apply(input: JavaClass) = input.reflect().isKotlinNotInternal()

    private fun Class<*>.isKotlinNotInternal() = isKotlinClass() && !isInternal()
}

fun beInternal() = object : ArchCondition<JavaClass>("checks class to be internal") {
    override fun check(item: JavaClass, events: ConditionEvents) {
        if (!item.reflect().isKotlinInternal()) {
            val message = "${item.fullName} is not internal"
            events.add(SimpleConditionEvent.violated(item, message))
        }
    }

    private fun Class<*>.isKotlinInternal() = isKotlinClass() && isInternal()
}

fun notBeInternal() = object : ArchCondition<JavaClass>("checks class to be not internal") {
    override fun check(item: JavaClass, events: ConditionEvents) {
        if (item.reflect().isKotlinInternal()) {
            val message = "${item.fullName} is internal"
            events.add(SimpleConditionEvent.violated(item, message))
        }
    }

    private fun Class<*>.isKotlinInternal() = isKotlinClass() && isInternal()
}

fun Class<*>.isInternal() = this.kotlin.visibility == KVisibility.INTERNAL

private fun Class<*>.isKotlinClass() = this.declaredAnnotations.any {
    it.annotationClass.qualifiedName == "kotlin.Metadata"
}
