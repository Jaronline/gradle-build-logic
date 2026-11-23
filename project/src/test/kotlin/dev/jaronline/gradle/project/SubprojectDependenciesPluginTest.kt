package dev.jaronline.gradle.project

import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.DocsType
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SubprojectDependenciesPluginTest {
	lateinit var project: Project

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		assertDoesNotThrow {
			project.pluginManager.apply(SubprojectDependenciesPlugin::class.java)
		}
	}

	@ParameterizedTest(name = "WHEN applying subproject dependencies plugin THEN {0} configuration is created correctly")
	@MethodSource("dependencyConfigurations")
	fun `WHEN applying subproject dependencies plugin THEN configuration is created correctly`(
		configurationName: String,
		attributes: Map<Attribute<Named>, String>,
	) {
		assertNotNull(project.configurations.findByName(configurationName))
		val configuration = project.configurations.getByName(configurationName)

		assertFalse(configuration.isCanBeConsumed)
		assertTrue(configuration.isCanBeResolved)

		attributes.entries.forEach { entry ->
			assertTrue(configuration.attributes.contains(entry.key))
			assertSame(project.objects.named(entry.key.type, entry.value), configuration.attributes.getAttribute(entry.key))
		}
	}

	companion object {
		@JvmStatic
		fun dependencyConfigurations(): List<Arguments> =
			listOf(
				Arguments.of(
					"dependencyClasses",
					mapOf(
						Category.CATEGORY_ATTRIBUTE to Category.LIBRARY,
						Usage.USAGE_ATTRIBUTE to Usage.JAVA_RUNTIME,
						LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE to LibraryElements.CLASSES,
					),
				),
				Arguments.of(
					"dependencyResources",
					mapOf(
						Category.CATEGORY_ATTRIBUTE to Category.LIBRARY,
						Usage.USAGE_ATTRIBUTE to Usage.JAVA_RUNTIME,
						LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE to LibraryElements.RESOURCES,
					),
				),
				Arguments.of(
					"dependencySources",
					mapOf(
						Category.CATEGORY_ATTRIBUTE to Category.DOCUMENTATION,
						Usage.USAGE_ATTRIBUTE to Usage.JAVA_RUNTIME,
						DocsType.DOCS_TYPE_ATTRIBUTE to DocsType.SOURCES,
					),
				),
			)
	}
}
