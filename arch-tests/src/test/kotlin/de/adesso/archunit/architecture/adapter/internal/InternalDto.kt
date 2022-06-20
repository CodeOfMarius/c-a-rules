package de.adesso.archunit.architecture.adapter.internal

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
internal class InternalDto {
    private fun internalDtos(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..port.in.internal..")
            .and()
            .resideInAPackage("..dto..")
    }

    private fun noInternalDtos(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..port.in.internal..")
            .and()
            .resideInAPackage("..dto..")
    }

    @ArchTest
    var dtosShouldBeNamedDtos: ArchRule = internalDtos()
        .should()
        .haveSimpleNameEndingWith("Dto")
        .orShould()
        .haveSimpleNameEndingWith("DtoOut")
        .orShould()
        .haveSimpleNameEndingWith("DtoIn")
        .because("Dtos should be named as such, to indicate what they are!")

    @ArchTest
    var dtoShouldNotDependOnOtherLayers: ArchRule = noInternalDtos()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            "..application..",
            "..domain..",
            "..adapter..")
        .because("Dtos are meant to transfer data to other modules, but should not expose other parts of the module in doing so!")

    @ArchTest
    var dtosShouldNotBeServices: ArchRule = noInternalDtos()
        .should()
        .beAnnotatedWith(Service::class.java)
        .orShould()
        .beAnnotatedWith(Component::class.java)
        .orShould()
        .beInterfaces()
        .because("Dtos are meant to represent data, therefore they should not be services or interfaces!")

    @ArchTest
    var internalPortsShouldBePublic: ArchRule = internalDtos()
        .should()
        .bePublic()
        .because("Internal ports are meant to expose the functions of a module, therefore they need to be public!")

    @ArchTest
    var fieldsOfAnAdapterShouldBePrivate: ArchRule = ArchRuleDefinition
        .fields()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.in.internal..")
        .and()
        .areDeclaredInClassesThat()
        .resideInAPackage("..dto..")
        .should()
        .bePrivate()
        .because("To prevent direct interactions with the dto, all attributes of an adapter must be private!")
        .allowEmptyShould(true)
}
