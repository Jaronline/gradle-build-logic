package dev.jaronline.gradle.kotlin

import org.gradle.api.plugins.JvmTestSuitePlugin
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.base.TestingExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Suppress("UnstableApiUsage")
class DependencyExtensionsTest {
	lateinit var testSuite: JvmTestSuite

	@BeforeEach
	fun setup() {
		val project = ProjectBuilder.builder().build()
		project.pluginManager.apply(JvmTestSuitePlugin::class.java)
		val testing = project.the<TestingExtension>()
		testSuite = testing.suites.register<JvmTestSuite>("test").get()
	}

	@Test
	fun `test kotlin(module) returns correct dependency notation`() {
		val result = testSuite.dependencies.kotlin("test")
		assertEquals("org.jetbrains.kotlin:kotlin-test", result)
	}

	@Test
	fun `test kotlin(module, version) returns correct dependency notation`() {
		val result = testSuite.dependencies.kotlin("test", "1.2.3")
		assertEquals("org.jetbrains.kotlin:kotlin-test:1.2.3", result)
	}
}
