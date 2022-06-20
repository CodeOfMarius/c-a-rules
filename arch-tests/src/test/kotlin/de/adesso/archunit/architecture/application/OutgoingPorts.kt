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
internal class OutgoingPorts {
    private fun ports(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..application.port.out..")
    }
    private fun noPorts(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..application.port.out..")
    }

    @ArchTest
    var portsShouldHaveValidName: ArchRule = ports()
        .should()
        .haveSimpleNameEndingWith("Port")
        .because("A port should be named as such, to indicate what they are!")
        .allowEmptyShould(true)

    @ArchTest
    var portsCasesShouldBeInterfaces: ArchRule = ports()
        .should()
        .beInterfaces()
        .because("This package is meant for interfaces as they are abstractions of the outgoing adapters!")
        .allowEmptyShould(true)

    @ArchTest
    var portsShouldNotDependOnAdapterOrOtherUseCases: ArchRule = noPorts()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            "..port.in.internal", "..application.in..", "..application.service..",
            "..adapter..", "..port.in..")
        .because("Abstractions of adapters should only depend on the domain model!")
        .allowEmptyShould(true)

    @ArchTest
    var portsShouldBeInternal: ArchRule = ports()
        .should(beInternal())
        .because("Abstractions of use cases should be internal so that no other module may access them!")
        .allowEmptyShould(true)
}
