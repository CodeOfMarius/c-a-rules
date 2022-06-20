package de.adesso.archunit.architecture.adapter.persistence

import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.beInternal
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import javax.persistence.Entity

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class EntityTest {
    private fun entities(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .areNotEnums()
            .and()
            .doNotHaveModifier(JavaModifier.ABSTRACT)
            .and()
            .resideInAPackage("..entities..")
    }
    private fun noEntities(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .areNotEnums()
            .and()
            .doNotHaveModifier(JavaModifier.ABSTRACT)
            .and()
            .resideInAPackage("..entities..")
    }

    @ArchTest
    var entitiesShouldBeAnnotatedEntities: ArchRule = entities()
        .should()
        .beAnnotatedWith(Entity::class.java)
        .because("Entities should be annotated with @Entity to mark them for persistence")

    @ArchTest
    var entitiesShouldNotBeServices: ArchRule = noEntities()
        .should()
        .beAnnotatedWith(Service::class.java)
        .orShould()
        .beAnnotatedWith(Component::class.java)
        .orShould()
        .beInterfaces()
        .because("Dtos are meant to represent data, therefore they should not be services or interfaces!")

    @ArchTest
    var entitiesShouldBeNamedEntities: ArchRule = entities()
        .should()
        .haveSimpleNameEndingWith("Entity")
        .because("Entities should be named entity for better recognition")

    @ArchTest
    var entitiesShouldNotDependOnDomain: ArchRule = noEntities()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..domain..", "..dto..")
        .because("Entities be independent from the domain or other dto")

    @ArchTest
    var entitiesShouldNotBePublic: ArchRule = entities()
        .should(beInternal())
        .because("Entities should be internal and not leak to other modules!")

    @ArchTest
    var fieldsOfEntitiesShouldBePrivate: ArchRule = ArchRuleDefinition
        .fields()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..entities..")
        .should()
        .bePrivate()
        .because("To prevent direct interactions with the dto, all attributes of an adapter must be private!")
        .allowEmptyShould(true)
}
