package dev.jaronline.gradle.project.readme

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertNotNull

class ReadmeTemplatePluginTest {
	lateinit var project: Project

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		assertDoesNotThrow {
			project.pluginManager.apply(ReadmeTemplatePlugin::class.java)
		}
	}

	@Test
	fun `WHEN applying README template plugin THEN creates correct tasks`() {
		assertNotNull(project.tasks.findByName("generateReadme"))
		assertInstanceOf<ProcessResources>(project.tasks.named("generateReadme").get())
		assertNotNull(project.tasks.findByName("updateReadme"))
		assertInstanceOf<Copy>(project.tasks.named("updateReadme").get())
		assertNotNull(project.tasks.findByName("readmeCheck"))
		assertInstanceOf<ReadmeCheck>(project.tasks.named("readmeCheck").get())
		assertNotNull(project.tasks.findByName("check"))
	}
}
