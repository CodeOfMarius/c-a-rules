package de.adesso.archunit.architecture.adapter.internal

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.beInternal
import de.adesso.archunit.architecture.util.internalInAdapterShouldImplementWithSameName
import de.adesso.archunit.architecture.util.haveOnlyPublicMethodsDeclaredInTheInterface
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class InternalInAdapter {
    private fun internalAdapters(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.in.internal")
    }

    private fun noInternalAdapters(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.in.internal")
    }

    @ArchTest
    var internalAdaptersShouldHaveValidName : ArchRule = internalAdapters()
        .should()
        .haveSimpleNameStartingWith("Internal")
        .andShould()
        .haveSimpleNameEndingWith("Adapter")
        .because("Adapters should be named as such, to indicate what they are!")
        .allowEmptyShould(true)

    @ArchTest
    var internalAdaptersShouldImplementAPort : ArchRule = internalAdapters()
        .should(internalInAdapterShouldImplementWithSameName())
        .because("An adapter should be named adapter to make it easier to identify it as an adapter. " +
                "Moreover if the adapter only implements one port it should be named after the port to make it more traceable in the source code!")
        .allowEmptyShould(true)

    @ArchTest
    var internalAdapterShouldNotDependOnOtherParts : ArchRule =
        noInternalAdapters()
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "..application.port.out..",
                "..application.service..",
                "..adapter.out..",
                "..adapter.in.web..",
                "..adapter.in.database.."
            )
            .because("An internal incoming adapter  should connect to a query or usecase. It should not skip the corresponding layers!")
            .allowEmptyShould(true)

    @ArchTest
    var internalAdaptersShouldOnlyHavePublicMethodsOfInterfaces: ArchRule = ArchRuleDefinition
        .methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.in.internal..")
        .and()
        .areNotPrivate()
        .should(haveOnlyPublicMethodsDeclaredInTheInterface())
        .because("To prevent direct interactions with the adapter, all functions must either be private or be part of its interface(s)!")
        .allowEmptyShould(true)

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
        .resideInAPackage("..adapter.out.database..")
        .and()
        .areDeclaredInClassesThat()
        .resideOutsideOfPackages("..entities..", "..repositories..")
        .should()
        .bePrivate()
        .because("To prevent direct interactions with the adapter, all attributes of an adapter must be private!")
        .allowEmptyShould(true)
}
