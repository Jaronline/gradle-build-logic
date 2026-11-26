package dev.jaronline.gradle.test.mockito

import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MockitoExtensionTest {
	lateinit var project: Project
	lateinit var extension: MockitoExtension

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		extension = project.extensions.create<MockitoExtension>("mockito")
	}

	@Test
	fun `test default extension contains version convention`() {
		assertEquals(DEFAULT_MOCKITO_VERSION, extension.version.orNull)
	}

	@Test
	fun `test MockitoExtension#library(artifact) returns mockito dependency notation with default version`() {
		val dependency = extension.library("core").get()
		assertEquals("org.mockito:mockito-core:$DEFAULT_MOCKITO_VERSION", dependency)
	}

	@Test
	fun `test MockitoExtension#library(artifact) returns mockito dependency notation with specified version`() {
		extension.version.set("1.3.2")
		val dependency = extension.library("core").get()
		assertEquals("org.mockito:mockito-core:1.3.2", dependency)
	}
}
