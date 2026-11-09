package dev.jaronline.gradle.kotlin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

class KotlinPluginTest {
	@Test
	fun `WHEN apply kotlin plugin THEN does not error`() {
		val project: Project = ProjectBuilder.builder().build()
		assertDoesNotThrow {
			project.pluginManager.apply(KotlinPlugin::class.java)
		}
	}
}
