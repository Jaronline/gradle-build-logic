package dev.jaronline.gradle.test

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
		fun `test junit(group, artifact) returns correct dependency notation`() {
			val result = project.dependencies.junit("jupiter", "jupiter-api")
			assertEquals("org.junit.jupiter:junit-jupiter-api", result)
		}

		@Test
		fun `test junit(group, artifact, version) returns correct dependency notation`() {
			val result = project.dependencies.junit("jupiter", "jupiter-api", "6.1.3")
			assertEquals("org.junit.jupiter:junit-jupiter-api:6.1.3", result)
		}

		@Test
		fun `test junitJupiter(artifact) returns correct dependency notation`() {
			val result = project.dependencies.junitJupiter("api")
			assertEquals("org.junit.jupiter:junit-jupiter-api", result)
		}

		@Test
		fun `test junitJupiter(artifact, version) returns correct dependency notation`() {
			val result = project.dependencies.junitJupiter("api", "6.1.3")
			assertEquals("org.junit.jupiter:junit-jupiter-api:6.1.3", result)
		}

		@Test
		fun `test junitPlatform(artifact) returns correct dependency notation`() {
			val result = project.dependencies.junitPlatform("launcher")
			assertEquals("org.junit.platform:junit-platform-launcher", result)
		}

		@Test
		fun `test junitPlatform(artifact, version) returns correct dependency notation`() {
			val result = project.dependencies.junitPlatform("launcher", "6.1.3")
			assertEquals("org.junit.platform:junit-platform-launcher:6.1.3", result)
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
		fun `test junit(group, artifact) returns correct dependency notation`() {
			val result = testSuite.dependencies.junit("jupiter", "jupiter-api")
			assertEquals("org.junit.jupiter:junit-jupiter-api", result)
		}

		@Test
		fun `test junit(group, artifact, version) returns correct dependency notation`() {
			val result = testSuite.dependencies.junit("jupiter", "jupiter-api", "6.1.3")
			assertEquals("org.junit.jupiter:junit-jupiter-api:6.1.3", result)
		}

		@Test
		fun `test junitJupiter(artifact) returns correct dependency notation`() {
			val result = testSuite.dependencies.junitJupiter("api")
			assertEquals("org.junit.jupiter:junit-jupiter-api", result)
		}

		@Test
		fun `test junitJupiter(artifact, version) returns correct dependency notation`() {
			val result = testSuite.dependencies.junitJupiter("api", "6.1.3")
			assertEquals("org.junit.jupiter:junit-jupiter-api:6.1.3", result)
		}

		@Test
		fun `test junitPlatform(artifact) returns correct dependency notation`() {
			val result = testSuite.dependencies.junitPlatform("launcher")
			assertEquals("org.junit.platform:junit-platform-launcher", result)
		}

		@Test
		fun `test junitPlatform(artifact, version) returns correct dependency notation`() {
			val result = testSuite.dependencies.junitPlatform("launcher", "6.1.3")
			assertEquals("org.junit.platform:junit-platform-launcher:6.1.3", result)
		}
	}
}
