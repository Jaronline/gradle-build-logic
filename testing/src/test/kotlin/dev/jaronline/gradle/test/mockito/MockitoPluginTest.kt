package dev.jaronline.gradle.test.mockito

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.configure
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MockitoPluginTest {
	lateinit var project: Project

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		assertDoesNotThrow {
			project.pluginManager.apply(MockitoPlugin::class.java)
		}
	}

	@Test
	fun `test mockito plugin creates mockito extension`() {
		val result = project.extensions.findByName("mockito")
		assertNotNull(result)
		assertInstanceOf<MockitoExtension>(result)
	}

	@Test
	fun `test mockito plugin creates mockitoAgent configuration correctly`() {
		val result = project.configurations.findByName("mockitoAgent")
		assertNotNull(result)
		assertFalse(result.isCanBeConsumed)
		assertTrue(result.isCanBeResolved)
	}

	@Test
	fun `test mockito plugin adds mockito core to mockitoAgent configuration dependencies correctly`() {
		val mockitoAgent = project.configurations.named("mockitoAgent").get()
		project.extensions.configure<MockitoExtension> {
			version.set("3.2.1")
		}

		assertEquals(1, mockitoAgent.dependencies.size)
		val dependency = mockitoAgent.dependencies.toTypedArray()[0]
		assertEquals("org.mockito", dependency.group)
		assertEquals("mockito-core", dependency.name)
		assertEquals("3.2.1", dependency.version)
		assertInstanceOf<ExternalModuleDependency>(dependency)
		assertFalse(dependency.isTransitive)
	}
}
