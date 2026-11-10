package dev.jaronline.gradle.publish

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertNotNull
import kotlin.test.Test

class PublishPluginTest {
	lateinit var project: Project

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		assertDoesNotThrow {
			project.pluginManager.apply(PublishPlugin::class.java)
		}
	}

	@Test
	fun `WHEN apply publish plugin THEN correct plugins are applied`() {
		val mavenPublishing = project.extensions.findByType<MavenPublishBaseExtension>()
		assertNotNull(mavenPublishing)
	}
}
