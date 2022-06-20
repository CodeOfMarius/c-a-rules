package de.adesso.archunit.architecture.adapter.web

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import de.adesso.archunit.architecture.util.dependOnClassesInTheAdapterPackage
import de.adesso.archunit.architecture.util.notUseDomainObjectAsParameters
import de.adesso.archunit.architecture.util.notUseDomainObjectAsReturnValue
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.RestController


@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
internal class Controller {
    private fun controller(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.in.web..")
            .and()
            .resideOutsideOfPackage("..dto..")
    }
    private fun noController(): GivenClassesConjunction {
        return ArchRuleDefinition.noClasses()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapter.in.web..")
            .and()
            .resideOutsideOfPackage("..dto..")
    }

    @ArchTest
    var controllerShouldBeAnnotatedWithRestController: ArchRule = controller()
        .should()
        .beAnnotatedWith(RestController::class.java)
        .because("Web-Controller should be annotated as a RestController, because that is a required technical detail!")

    @ArchTest
    var controllerShouldBeNamedController: ArchRule = controller()
        .should()
        .haveSimpleNameEndingWith("Controller")
        .because("Web-Controller should be named as such, to indicate what they are!")

    @ArchTest
    var controllerShouldNotDependOnPortsOrAdapter: ArchRule = noController()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            "..adapter.in.internal..",
            "..adapter.out..",
            "..application.port.out..",
            "..application.service..")
        .because("Controller may only access from the frontend!")

    @ArchTest
    var servicesShouldNotDependOnOtherServicesDirectly: ArchRule = noController()
        .should(dependOnClassesInTheAdapterPackage("controller"))
        .because("Controller should be atomic and only use queries or usecases!")

    @ArchTest
    var controllerShouldNotUseDomainsAsParameter: ArchRule = ArchRuleDefinition
        .methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.in.web..")
        .and()
        .areDeclaredInClassesThat()
        .resideOutsideOfPackages("..dto..")
        .and()
        .areNotPrivate()
        .should(notUseDomainObjectAsParameters())
        .because("The domain model should not be exposed by the web-layer to isolated it from the frontend!")

    @ArchTest
    var controllerShouldNotUseDomainsAsReturnValues: ArchRule = ArchRuleDefinition
        .methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.in.web..")
        .and()
        .areDeclaredInClassesThat()
        .resideOutsideOfPackages("..dto..")
        .and()
        .areNotPrivate()
        .should(notUseDomainObjectAsReturnValue())
        .because("The domain model should be isolated from the web layer")

    @ArchTest
    var endpointsShouldBeDocumented: ArchRule = ArchRuleDefinition
        .methods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.in.web..")
        .and()
        .areDeclaredInClassesThat()
        .resideOutsideOfPackages("..dto..")
        .and()
        .areNotPrivate()
        .should()
        .beAnnotatedWith(Operation::class.java)
        .because("The domain model should be isolated from the web layer")

    @ArchTest
    var fieldsOfControllerShouldBePrivate: ArchRule = ArchRuleDefinition
        .fields()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..adapter.in.web..")
        .and()
        .areDeclaredInClassesThat()
        .resideOutsideOfPackages("..dto..")
        .should()
        .bePrivate()
        .because("To prevent direct interactions with the controller, all attributes of an adapter must be private!")
        .allowEmptyShould(true)
}
