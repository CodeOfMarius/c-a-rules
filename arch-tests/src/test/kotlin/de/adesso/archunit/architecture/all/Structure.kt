package de.adesso.archunit.architecture.all

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.springframework.boot.autoconfigure.SpringBootApplication


@AnalyzeClasses(
    packages = ["de.adesso.archunit"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
internal class Structure {
    private fun classes(): GivenClassesConjunction {
        return ArchRuleDefinition.classes()
            .that()
            .areTopLevelClasses()
            .and()
            .areNotAnnotatedWith(SpringBootApplication::class.java)
            .and()
            .doNotHaveSimpleName("ArchUnitApplicationKt")
    }

    @ArchTest
    var allClassesMustBePartOfTheStructure: ArchRule =
        classes()
            .should()
            .resideInAnyPackage("..domain..", "..application..", "..adapter..", "..port..", "..config..")
}
