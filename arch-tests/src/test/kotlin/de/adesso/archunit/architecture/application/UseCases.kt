package de.adesso.archunit.architecture.application

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.beInternal
import de.adesso.archunit.architecture.util.dependOnClassesInTheServicePackage
import de.adesso.archunit.architecture.util.haveOnlyOneInterface
import de.adesso.archunit.architecture.util.haveOnlyPublicMethodsDeclaredInTheInterface
import org.springframework.stereotype.Service

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
internal class UseCases {
    private fun services(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .resideInAPackage("..application.service..")
    }

    private fun noServices(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage("..application.service..")
    }

    @ArchTest
    var servicesShouldBeServices: ArchRule = services()
        .should()
        .beAnnotatedWith(Service::class.java)
        .because("Services should be annotated as Spring services, because they have to be managed by spring!")

    @ArchTest
    var servicesShouldBeNamedService: ArchRule = services()
        .should()
        .haveSimpleNameEndingWith("Service")
        .because("Services should be named as such, to indicate what they are!")

    @ArchTest
    var servicesShouldNotBeInterfaces: ArchRule = services()
        .should()
        .beTopLevelClasses()
        .andShould()
        .notBeInterfaces()
        .because("Services should be top level classes and not interfaces!")

    @ArchTest
    var servicesShouldOnlyHaveOneInterface: ArchRule = services()
        .should(haveOnlyOneInterface())
        .because("Every usecase should have exactly one interface so that no classes need to depend directly on the usecase!")

    @ArchTest
    var servicesShouldNotDependOnAdapter: ArchRule = noServices()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..port.in.internal..", "..adapter..")
        .because("Services should use interfaces and not the concrete implementations of adapter!")

    @ArchTest
    var servicesShouldOnlyDependOnDomainAndAbstractions: ArchRule = services()
        .should()
        .onlyDependOnClassesThat()
        .resideInAnyPackage("..domain..", "..service..", "..application.port..",
            "org.springframework.stereotype", "org.slf4j",
            "java.lang..", "java.time..", "java.util..", "org.jetbrains.annotations..", "kotlin..")
        .because("Services should only depend on the domain and other ports!")

    @ArchTest
    var servicesShouldNotDependOnOtherServicesDirectly: ArchRule = noServices()
        .should(dependOnClassesInTheServicePackage())
        .because("Services should be atomic and only use incoming or outgoing ports!")

    @ArchTest
    var serviceShouldOnlyHavePublicMethodsOfInterfaces: ArchRule = ArchRuleDefinition
        .methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..application.service..")
        .and()
        .areNotPrivate()
        .should(haveOnlyPublicMethodsDeclaredInTheInterface())
        .because("Services should only offer functions, that are part of its interface to prevent direct access to the service!")

    @ArchTest
    var useCasesShouldNotBePublic: ArchRule = services()
        .should(beInternal())
        .because("To prevent leakage from this module, a service should be internal!")

    @ArchTest
    var fieldsOfServicesShouldBePrivate: ArchRule = ArchRuleDefinition
        .fields()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..application.service..")
        .should()
        .bePrivate()
        .because("To prevent direct interactions with the service, all attributes of an adapter must be private!")
        .allowEmptyShould(true)
}
