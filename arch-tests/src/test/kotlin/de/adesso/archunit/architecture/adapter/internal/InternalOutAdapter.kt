package de.adesso.archunit.architecture.adapter.internal

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.beInternal
import de.adesso.archunit.architecture.util.haveOnlyPublicMethodsDeclaredInTheInterface
import de.adesso.archunit.architecture.util.internalOutAdapterShouldImplementWithSameName
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class InternalOutAdapter {
    private fun internalAdapters(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.out.internal..")
    }

    private fun noInternalAdapters(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.out.internal..")
    }

    @ArchTest
    var internalAdaptersShouldBeNamedRight : ArchRule = internalAdapters()
        .should()
        .haveSimpleNameStartingWith("Internal")
        .andShould()
        .haveSimpleNameEndingWith("Adapter")

    @ArchTest
    var internalAdaptersShouldImplementAPort : ArchRule = internalAdapters()
        .should(internalOutAdapterShouldImplementWithSameName())

    @ArchTest
    var internalAdapterShouldNotDependOnOutgoingAdapters : ArchRule =
        noInternalAdapters()
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "..application.port.in..",
                "..application.service..",
                "..adapter.in..",
                "..adapter.out.database..",
                "..adapter.out.generic.."
            )

    @ArchTest
    var internalAdaptersShouldOnlyHavePublicMethodsOfInterfaces: ArchRule = ArchRuleDefinition
        .methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.out.internal")
        .and()
        .areNotPrivate()
        .should(haveOnlyPublicMethodsDeclaredInTheInterface())

    @ArchTest
    var internalAdaptersShouldBeInternal: ArchRule = internalAdapters()
        .should(beInternal())
        .because("To prevent leakage of the module, the internal adapters should be internal!")
        .allowEmptyShould(true)

    @ArchTest
    var internalAdapterShouldBeManagedBySpring: ArchRule = internalAdapters()
        .should()
        .beAnnotatedWith(Service::class.java)
        .orShould()
        .beAnnotatedWith(Component::class.java)
        .because("An adapter should be a component managed by spring so that you can inject it via its interface!")
        .allowEmptyShould(true)

    @ArchTest
    var fieldsOfAnAdapterShouldBePrivate: ArchRule = ArchRuleDefinition
        .fields()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.out.internal..")
        .should()
        .bePrivate()
        .because("To prevent direct interactions with the adapter, all attributes of an adapter must be private!")
        .allowEmptyShould(true)
}
