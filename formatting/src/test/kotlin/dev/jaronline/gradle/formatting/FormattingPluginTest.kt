package dev.jaronline.gradle.formatting

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertNotNull
import kotlin.test.Test

class FormattingPluginTest {
	lateinit var project: Project

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		assertDoesNotThrow {
			project.pluginManager.apply(FormattingPlugin::class.java)
		}
	}

	@Test
	fun `WHEN apply formatting plugin THEN correct plugins are applied`() {
		val spotless = project.extensions.findByType<SpotlessExtension>()
		assertNotNull(spotless)
	}
}
