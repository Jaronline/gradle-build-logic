package dev.jaronline.gradle.project.readme

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.kotlin.dsl.register
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.io.File
import kotlin.test.assertEquals

class ReadmeCheckTest {
	lateinit var project: Project

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
	}

	@Test
	fun `test checkReadme without root README throws GradleException`() {
		val generatedReadme = Mockito.mock(RegularFile::class.java)
		val generatedReadmeFile = Mockito.mock(File::class.java)
		Mockito.`when`(generatedReadme.asFile).thenReturn(generatedReadmeFile)
		val readmeCheck =
			project.tasks.register<ReadmeCheck>("readmeCheck") {
				this.generatedReadme.set(generatedReadme)
			}

		val exception =
			assertThrows<GradleException> {
				readmeCheck.get().checkReadme()
			}
		assertEquals(
			"Could not find root README.md file. Generate the README.md file using the updateReadme task",
			exception.message,
		)
	}

	@Test
	fun `test checkReadme with root README AND root README is not a file throws GradleException`() {
		val generatedReadme = Mockito.mock(RegularFile::class.java)
		val generatedReadmeFile = Mockito.mock(File::class.java)
		Mockito.`when`(generatedReadme.asFile).thenReturn(generatedReadmeFile)

		val rootReadme = Mockito.mock(RegularFile::class.java)
		val rootReadmeFile = Mockito.mock(File::class.java)
		Mockito.`when`(rootReadmeFile.isFile).thenReturn(false)
		Mockito.`when`(rootReadme.asFile).thenReturn(rootReadmeFile)

		val readmeCheck =
			project.tasks.register<ReadmeCheck>("readmeCheck") {
				this.generatedReadme.set(generatedReadme)
				this.rootReadme.set(rootReadme)
			}

		val exception =
			assertThrows<GradleException> {
				readmeCheck.get().checkReadme()
			}
		assertEquals(
			"Could not find root README.md file. Generate the README.md file using the updateReadme task",
			exception.message,
		)
	}
}
