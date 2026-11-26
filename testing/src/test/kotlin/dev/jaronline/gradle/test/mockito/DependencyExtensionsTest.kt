package dev.jaronline.gradle.test.mockito

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DependencyExtensionsTest {
	lateinit var project: Project

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
	}

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
