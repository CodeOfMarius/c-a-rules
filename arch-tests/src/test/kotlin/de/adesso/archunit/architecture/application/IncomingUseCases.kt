package de.adesso.archunit.architecture.application

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.beInternal

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
internal class IncomingUseCases {
    private fun useCases(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .resideInAPackage("..application.port.in.usecase..")
    }

    private fun noUseCases(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage("..application.port.in.usecase..")
    }

    @ArchTest
    var incomingUseCasesShouldBeNamedQueries: ArchRule = useCases()
        .should()
        .haveSimpleNameEndingWith("UseCase")
        .because("A usecase should be named as such, to indicate what they are!")
        .allowEmptyShould(true)

    @ArchTest
    var incomingUseCasesShouldBeInterfaces: ArchRule = useCases()
        .should()
        .beTopLevelClasses()
        .andShould()
        .beInterfaces()
        .because("This package is meant for interfaces as they are abstractions of the use cases")
        .allowEmptyShould(true)

    @ArchTest
    var incomingUseCasesShouldNotDependOnAdapter: ArchRule = noUseCases()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..port.in.internal", "..application.service..", "..adapter..", "..port.out..")
        .because("Abstractions of use cases should only depend on the domain model!")
        .allowEmptyShould(true)

    @ArchTest
    var incomingUseCasesShouldNotBePublic: ArchRule = useCases()
        .should(beInternal())
        .because("Abstractions of use cases should be internal so that no other module may access them!")
        .allowEmptyShould(true)
}
