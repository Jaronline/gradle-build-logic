package dev.jaronline.gradle.test.mockito

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.register
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import org.gradle.api.tasks.testing.Test as TestTask

class TaskExtensionsTest {
	lateinit var project: Project

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
	}

	@Test
	fun `test Test#useMockitoAgent() on project without MockitoPlugin throws GradleException`() {
		val testTask = project.tasks.register<TestTask>("test").get()

		val exception =
			assertThrows<GradleException> {
				testTask.useMockitoAgent()
			}
		assertEquals(
			"useMockitoAgent() can only be used when the dev.jaronline.test.mockito plugin has been applied!",
			exception.message,
		)
	}

	@Test
	fun `test Test#useMockitoAgent() on project with MockitoPlugin does not throw`() {
		project.pluginManager.apply(MockitoPlugin::class.java)
		val testTask = project.tasks.register<TestTask>("test").get()

		assertDoesNotThrow {
			testTask.useMockitoAgent()
		}
	}

	@Test
	fun `test Test#useMockitoAgent() on project with MockitoPlugin adds MockitoAgentProvider correctly`() {
		project.pluginManager.apply(MockitoPlugin::class.java)
		val agentConfiguration = project.configurations.named("mockitoAgent").get()

		val testTask =
			project.tasks
				.register<TestTask>("test") {
					useMockitoAgent()
				}.get()

		assertEquals(1, testTask.jvmArgumentProviders.size)
		val provider = testTask.jvmArgumentProviders[0]
		assertInstanceOf<MockitoAgentProvider>(provider)
		assertEquals(1, provider.agentJar.from.size)
		val from = provider.agentJar.from.toList()[0]
		assertInstanceOf<Provider<Configuration>>(from)
		assertEquals(agentConfiguration, from.get())
	}
}
