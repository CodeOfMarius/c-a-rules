package de.adesso.archunit.architecture.adapter.internal

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class IncomingPort {
    private fun internalPorts(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..port.in.internal..")
            .and()
            .resideOutsideOfPackages("..dto..")
    }

    private fun noInternalPorts(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..port.in.internal..")
            .and()
            .resideOutsideOfPackages("..dto..")
    }

    @ArchTest
    var internalPortsShouldBeNamedQueriesSuffix: ArchRule = internalPorts()
        .should()
        .haveSimpleNameEndingWith("Port")
        .because("Internal Ports should be named as such!")
        .allowEmptyShould(true)

    @ArchTest
    var internalPortsShouldBeNamedQueriesPrefix: ArchRule = internalPorts()
        .should()
        .haveSimpleNameStartingWith("Internal")
        .because("Internal Ports should be named as such!")
        .allowEmptyShould(true)

    @ArchTest
    var internalPortsShouldBeInterfaces: ArchRule = internalPorts()
        .should()
        .beInterfaces()
        .because("So that other modules can access this module, they need an abstraction." +
                "This abstraction should be an interface!")
        .allowEmptyShould(true)

    @ArchTest
    var internalPortsShouldBeIndependentFromOtherParts: ArchRule = noInternalPorts()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..adapter..", "..application..", "..domain..")
        .because("Internal ports should not expose any other parts of the module, therefore they should not depend on other layers!")
        .allowEmptyShould(true)

    @ArchTest
    var internalPortsShouldBePublic: ArchRule = internalPorts()
        .should()
        .bePublic()
        .because("Internal ports are meant to expose the functions of a module, therefore they need to be public!")
        .allowEmptyShould(true)
}
