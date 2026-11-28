package dev.jaronline.gradle.reporting.aggregation

import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class JacocoAggregationExtensionTest {
	lateinit var project: Project
	lateinit var extension: JacocoAggregationExtension

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		extension = project.extensions.create<JacocoAggregationExtension>("aggregation")
	}

	@Test
	fun `test testSuites convention value is test`() {
		assertEquals(1, extension.testSuites.get().size)
		assertEquals("test", extension.testSuites.get().elementAt(0))
	}

	@Test
	fun `test testSuite(suiteName) sets single test suite`() {
		extension.testSuite("integrationTest")
		assertEquals(1, extension.testSuites.get().size)
		assertEquals("integrationTest", extension.testSuites.get().elementAt(0))
	}

	@ParameterizedTest
	@MethodSource("suiteNames")
	fun `test testSuite(suiteName) adds multiple test suites`(suiteNames: List<String>) {
		suiteNames.forEach {
			extension.testSuite(it)
		}
		assertEquals(suiteNames.size, extension.testSuites.get().size)
		assertEquals(suiteNames, extension.testSuites.get().toList())
	}

	@Test
	fun `test testSuites(suiteNames) sets test suites`() {
		extension.testSuites("smokeTest", "integrationTest")
		assertEquals(2, extension.testSuites.get().size)
		assertEquals(listOf("smokeTest", "integrationTest"), extension.testSuites.get().toList())
	}

	@Test
	fun `test testSuites(suiteNames) overwrites test suites`() {
		extension.testSuites("smokeTest", "integrationTest")
		extension.testSuites("test", "functionalTest")
		assertEquals(2, extension.testSuites.get().size)
		assertEquals(listOf("test", "functionalTest"), extension.testSuites.get().toList())
	}

	companion object {
		@JvmStatic
		fun suiteNames(): List<Arguments> =
			listOf(
				Arguments.of(listOf("smokeTest")),
				Arguments.of(listOf("smokeTest", "functionalTest")),
				Arguments.of(listOf("smokeTest", "test", "integrationTest", "functionalTest")),
			)
	}
}
