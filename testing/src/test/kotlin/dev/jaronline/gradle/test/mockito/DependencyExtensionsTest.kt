package dev.jaronline.gradle.test.mockito

import org.gradle.api.Project
import org.gradle.api.plugins.JvmTestSuitePlugin
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.base.TestingExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DependencyExtensionsTest {
	lateinit var project: Project

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
	}

	@Nested
	inner class DependencyHandlerTests {
		@Test
		fun `test mockito(artifact) returns correct dependency notation`() {
			val result = project.dependencies.mockito("core")
			assertEquals("org.mockito:mockito-core", result)
		}

		@Test
		fun `test mockito(artifact, version) returns correct dependency notation`() {
			val result = project.dependencies.mockito("core", "5.24.0")
			assertEquals("org.mockito:mockito-core:5.24.0", result)
		}
	}

	@Nested
	@Suppress("UnstableApiUsage")
	inner class JvmComponentDependenciesTests {
		lateinit var testSuite: JvmTestSuite

		@BeforeEach
		fun setup() {
			project.pluginManager.apply(JvmTestSuitePlugin::class.java)
			val testing = project.the<TestingExtension>()
			testSuite = testing.suites.register<JvmTestSuite>("test").get()
		}

		@Test
		fun `test mockito(artifact) returns correct dependency notation`() {
			val result = testSuite.dependencies.mockito("core")
			assertEquals("org.mockito:mockito-core", result)
		}

		@Test
		fun `test mockito(artifact, version) returns correct dependency notation`() {
			val result = testSuite.dependencies.mockito("core", "5.24.0")
			assertEquals("org.mockito:mockito-core:5.24.0", result)
		}
	}
}
