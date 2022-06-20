package de.adesso.archunit.architecture.domain

import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.onlyHaveSimpleMethods
import de.adesso.archunit.architecture.util.beInternal

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class DomainTest {

    private fun domain(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .areNotEnums()
            .and()
            .doNotHaveModifier(JavaModifier.ABSTRACT)
            .and()
            .resideInAPackage("..domain")
    }
    private fun noDomain(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .areNotEnums()
            .and()
            .doNotHaveModifier(JavaModifier.ABSTRACT)
            .and()
            .resideInAPackage("..domain")
    }

    @ArchTest
    var domainShouldHaveSimpleName: ArchRule = noDomain()
        .should()
        .haveSimpleNameEndingWith("Entity")
        .andShould()
        .haveSimpleNameEndingWith("Service")
        .andShould()
        .haveSimpleNameEndingWith("Adapter")
        .andShould()
        .haveSimpleNameEndingWith("Repository")
        .andShould()
        .haveSimpleNameEndingWith("Port")
        .andShould()
        .haveSimpleNameEndingWith("UseCase")
        .andShould()
        .haveSimpleNameEndingWith("Query")
        .because("Domain objects should not have a name that suggest that they are not domain objects!")

    @ArchTest
    var domainShouldNotDependOnOtherLayers: ArchRule = noDomain()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..adapter..", "..application..", "..port..")
        .because("Domain objects should not depend on other layers or technical details!")

    @ArchTest
    var domainShouldOnlyDependOnDomain: ArchRule = domain()
        .should()
        .onlyDependOnClassesThat()
        .resideInAnyPackage("..domain..", "org.jetbrains.annotations..", "kotlin..", "java.lang..", "java.util..", "java.time..")
        .because("Domain objects should not depend on other layers or technical details!")

    @ArchTest
    var domainShouldBeInternal: ArchRule = domain()
        .should(beInternal())
        .because("Domain objects should not be publicly available!")

    @ArchTest
    var domainShouldHaveOnlySimpleMethods: ArchRule = ArchRuleDefinition
        .methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..domain..")
        .should(onlyHaveSimpleMethods())
        .because("The project uses an anemic domain model and therefore should only have simple methods and not business logic")

    @ArchTest
    var fieldsOfTheDomainShouldBePrivate: ArchRule = ArchRuleDefinition
        .fields()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..domain..")
        .should()
        .bePrivate()
        .because("To prevent direct interactions with the dto, all attributes of an adapter must be private!")
        .allowEmptyShould(true)
}
