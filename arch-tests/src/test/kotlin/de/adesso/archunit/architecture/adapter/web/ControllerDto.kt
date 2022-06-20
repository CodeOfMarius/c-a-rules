package de.adesso.archunit.architecture.adapter.web

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.beInternal
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
internal class ControllerDto {
    private fun dtos(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.in.web..")
            .and()
            .resideInAPackage("..dto..")
    }

    private fun noDtos(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.in.web..")
            .and()
            .resideInAPackage("..dto..")
    }

    @ArchTest
    var dtosShouldBeNamedDtos: ArchRule = dtos()
        .should()
        .haveSimpleNameEndingWith("Dto")
        .orShould()
        .haveSimpleNameEndingWith("DtoOut")
        .orShould()
        .haveSimpleNameEndingWith("DtoIn")
        .because("Dto's should be named as such, to indicate what they are!")

    @ArchTest
    var dtosShouldBeDocumented: ArchRule = ArchRuleDefinition.fields()
        .that()
        .areDeclaredInClassesThat()
        .resideInAnyPackage("..adapter.in.web.dto..")
        .should()
        .beAnnotatedWith(Schema::class.java)
        .because("The frontend communicates via these classes with the backend, therefore they should be well documented!")

    @ArchTest
    var dtoShouldNotDependOnOtherLayers: ArchRule = noDtos()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            "..application..",
            "..domain..",
            "..adapter.out..",
            "..adapter.in.internal..")
        .because("Dtos are meant to represent data and should not expose other parts of the module!")

    @ArchTest
    var dtoShouldNotBeServices: ArchRule = noDtos()
        .should()
        .beAnnotatedWith(Service::class.java)
        .orShould()
        .beAnnotatedWith(Component::class.java)
        .orShould()
        .beInterfaces()
        .because("Dtos are meant to represent data, therefore they should not be services or interfaces!")

    @ArchTest
    var dtoShouldBeInternal: ArchRule = dtos()
        .should(beInternal())
        .because("Abstractions of use cases should be internal so that no other module may access them!")
}
