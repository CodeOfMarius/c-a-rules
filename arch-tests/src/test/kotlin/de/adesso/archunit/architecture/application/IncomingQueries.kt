package de.adesso.archunit.architecture.application

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.returnSomething
import de.adesso.archunit.architecture.util.beInternal

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
internal class IncomingQueries {
    private fun queries(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .resideInAPackage("..application.port.in.query..")
    }

    private fun noQueries(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage("..application.port.in.query..")
    }

    @ArchTest
    var queriesShouldBeNamedQueries: ArchRule = queries()
        .should()
        .haveSimpleNameEndingWith("Query")
        .because("Queries should be named as such, to indicate what they are!")
        .allowEmptyShould(true)

    @ArchTest
    var queriesShouldBeInterfaces: ArchRule = queries()
        .should()
        .beTopLevelClasses()
        .andShould()
        .beInterfaces()
        .because("This package is meant for the abstractions of reading use cases (queries)!")
        .allowEmptyShould(true)

    @ArchTest
    var queriesShouldNotDependOnAdapter: ArchRule = noQueries()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..port.in.internal..", "..adapter..", "..port.out..", "..application.service..")
        .because("Queries should not depend on adapters or outgoing ports")
        .allowEmptyShould(true)

    @ArchTest
    var queriesShouldBeReadingFunctions: ArchRule = ArchRuleDefinition
        .methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..application.port.in.query..")
        .and()
        .areNotPrivate()
        .should(returnSomething())
        .because("A function that is part of a reading interface should return the read value!")
        .allowEmptyShould(true)

    @ArchTest
    var queriesShouldBeInternal: ArchRule = queries()
        .should(beInternal())
        .because("Abstractions of use cases should be internal so that no other module may access them!")
}
