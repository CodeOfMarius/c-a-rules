package de.adesso.archunit.architecture.adapter.generic

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.adapterShouldImplementWithSameName
import de.adesso.archunit.architecture.util.beInternal
import de.adesso.archunit.architecture.util.haveOnlyPublicMethodsDeclaredInTheInterface
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class GenericAdapter {
    private fun genericAdapters(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.out..")
            .and()
            .resideOutsideOfPackages("..database..", "..internal..", "..dto..")
    }

    private fun noGenericAdapters(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.out..")
            .and()
            .resideOutsideOfPackages("..database..", "..internal..", "..dto..")
    }

    @ArchTest
    var genericOutgoingAdaptersShouldHaveValidName : ArchRule = genericAdapters()
        .should(adapterShouldImplementWithSameName())
        .because("An adapter should be named adapter to make it easier to identify it as an adapter. " +
                "Moreover if the adapter only implements one port it should be named after the port to make it more traceable in the source code!")

    @ArchTest
    var genericOutgoingAdaptersShouldNotDependOnUseCasesOrIncomingAdapters : ArchRule =
        noGenericAdapters()
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "..application.port.in..",
                "..application.service..",
                "..adapter.in..",
                "..adapter.out.database..",
                "..adapter.out.internal.."
            )
            .because("A generic outgoing adapter is supposed to connect to another system. " +
                    "Therefore it should not depend on other parts of the sourcecode. This prevents leakage of the technical details from the adapter!")

    @ArchTest
    var genericOutgoingAdaptersShouldOnlyHavePublicMethodsOfTheInterface: ArchRule = ArchRuleDefinition
        .methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.out..")
        .and()
        .areDeclaredInClassesThat()
        .resideOutsideOfPackages("..database..", "..internal..", "..dto..")
        .and()
        .areNotPrivate()
        .should(haveOnlyPublicMethodsDeclaredInTheInterface())
        .because("To prevent direct interactions with the adapter, all functions must either be private or be part of its interface(s)!")

    @ArchTest
    var fieldsOfAnAdapterShouldBePrivate: ArchRule = ArchRuleDefinition
        .fields()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.out..")
        .and()
        .areDeclaredInClassesThat()
        .resideOutsideOfPackages("..database..", "..internal..", "..dto..")
        .should()
        .bePrivate()
        .because("To prevent direct interactions with the adapter, all attributes of an adapter must be private!")
        .allowEmptyShould(true)

    @ArchTest
    var adapterShouldNotBePublic: ArchRule = genericAdapters()
        .should(beInternal())
        .because("To prevent other modules from accessing this adapter directly, it should be internal!")

    @ArchTest
    var adapterShouldBeManagedBySpring: ArchRule = genericAdapters()
        .should()
        .beAnnotatedWith(Service::class.java)
        .orShould()
        .beAnnotatedWith(Component::class.java)
        .because("An adapter should be a component managed by spring so that you can inject it via its interface!")
}
