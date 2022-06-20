package de.adesso.archunit.architecture.adapter.persistence

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.adapterShouldImplementWithSameName
import de.adesso.archunit.architecture.util.beInternal
import de.adesso.archunit.architecture.util.dependOnClassesInTheAdapterPackage
import de.adesso.archunit.architecture.util.haveOnlyPublicMethodsDeclaredInTheInterface
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
internal class PersistenceAdapter {
    private fun persistenceAdapter(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.out.database..")
            .and()
            .resideOutsideOfPackages("..entities..", "..repositories..")
    }
    private fun noPersistenceAdapter(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.out.database..")
            .and()
            .resideOutsideOfPackages("..entities..", "..repositories..")
    }

    @ArchTest
    var adaptersShouldBeAnnotatedAsService: ArchRule = persistenceAdapter()
        .should()
        .beAnnotatedWith(Service::class.java)
        .orShould()
        .beAnnotatedWith(Component::class.java)
        .because("An adapter should be a component managed by spring so that you can inject it via its interface!")

    @ArchTest
    var persistenceAdaptersShouldNotDependOnOtherServicesDirectly: ArchRule = noPersistenceAdapter()
        .should(dependOnClassesInTheAdapterPackage("adapter.out.persistence"))
        .because("Persistence adapters should not depend on other adapters.")

    @ArchTest
    var persistenceAdaptersShouldOnlyHavePublicMethodsOfInterfaces: ArchRule = ArchRuleDefinition
        .methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.out.database..")
        .and()
        .areDeclaredInClassesThat()
        .resideOutsideOfPackages("..entities..", "..repositories..")
        .and()
        .areNotPrivate()
        .should(haveOnlyPublicMethodsDeclaredInTheInterface())
        .because("Services should only offer functions, that are part of its interface")
        .allowEmptyShould(true)

    @ArchTest
    var persistenceAdaptersShouldImplementAPort : ArchRule = persistenceAdapter()
        .should(adapterShouldImplementWithSameName())
        .allowEmptyShould(true)

    @ArchTest
    var persistenceAdaptersShouldBeInternal: ArchRule = persistenceAdapter()
        .should(beInternal())
        .because("Abstractions of use cases should be internal so that no other module may access them!")
        .allowEmptyShould(true)

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
}
