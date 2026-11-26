package dev.jaronline.gradle.test

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.register
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TaskExtensionsTest {
	@Test
	fun `test defaultTestLogging sets default logging parameters`() {
		val project = ProjectBuilder.builder().build()

		val task =
			project.tasks
				.register<org.gradle.api.tasks.testing.Test>("test") {
					defaultTestLogging()
				}.get()

		assertEquals(setOf(TestLogEvent.FAILED), task.testLogging.events)
		assertEquals(TestExceptionFormat.FULL, task.testLogging.exceptionFormat)
	}
}
