package de.adesso.archunit.architecture.adapter.generic

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.beInternal
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class GenericDto {
    private fun genericDto(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.out..")
            .and()
            .resideInAPackage("..dto..")
            .and()
            .resideOutsideOfPackages("..database..", "..internal..")
    }

    private fun noGenericDto(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.out..")
            .and()
            .resideInAPackage("..dto..")
            .and()
            .resideOutsideOfPackages("..database..", "..internal..")
    }

    @ArchTest
    var dtosShouldNotDependOnOtherClasses : ArchRule =
        noGenericDto()
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "..application.port.in..",
                "..application.service..",
                "..adapter.in..",
                "..adapter.out.database..",
                "..adapter.out.internal..",
                "..domain.."
            )
            .because("The dtos used by the outgoing adapters should be independent from other parts of the module!")

    @ArchTest
    var dtosShouldBeInternal: ArchRule = genericDto()
        .should(beInternal())
        .because("Dtos of outgoing adapters should not be used by other modules, so they have to be internal!")

    @ArchTest
    var dtosShouldHaveValidName: ArchRule = genericDto()
        .should()
        .haveSimpleNameEndingWith("Dto")
        .because("Dto's should be named as such, to indicate what they are!")

    @ArchTest
    var dtosShouldNotBeServices: ArchRule = noGenericDto()
        .should()
        .beAnnotatedWith(Service::class.java)
        .orShould()
        .beAnnotatedWith(Component::class.java)
        .orShould()
        .beInterfaces()
        .because("Dtos are meant to represent data, therefore they should not be services or interfaces!")

}
