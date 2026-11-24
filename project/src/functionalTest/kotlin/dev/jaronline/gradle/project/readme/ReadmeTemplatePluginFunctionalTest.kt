package dev.jaronline.gradle.project.readme

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.net.URL

class ReadmeTemplatePluginFunctionalTest {
	@field:TempDir
	lateinit var projectDir: File

	private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }
	private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
	private val readmeTemplate by lazy { projectDir.resolve("src/main/resources/README.md") }
	private val generatedReadme by lazy { projectDir.resolve("build/generated/readme/README.md") }
	private val rootReadme by lazy { projectDir.resolve("README.md") }

	@BeforeEach
	fun setup() {
		settingsFile.writeText("")
		buildFile.writeText(
			"""
			plugins {
				id("dev.jaronline.project.readme-template")
			}
			
			""".trimIndent(),
		)
		readmeTemplate.parentFile.mkdirs()
		generatedReadme.parentFile.mkdirs()
	}

	@Test
	@DisplayName("GIVEN there is no README template WHEN executing generateReadme task THEN task outcome is NO_SOURCE")
	fun `test generateReadme outcome is NO_SOURCE without README template`() {
		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("generateReadme")
				.forwardOutput()
				.build()

		assertEquals(TaskOutcome.NO_SOURCE, result.task(":generateReadme")?.outcome)
	}

	@Test
	@DisplayName(
		"GIVEN README template exists AND README contains no properties " +
			"WHEN executing generateReadme task THEN generates README successfully",
	)
	fun `test generateReadme generates README with README template AND without properties`() {
		readmeTemplate.writeText(getResource("generate/no-properties/README-template.md")!!.readText())

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("generateReadme")
				.forwardOutput()
				.build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":generateReadme")?.outcome)
		assertEquals(getResource("generate/no-properties/README-expected.md")!!.readText(), generatedReadme.readText())
	}

	@Test
	@DisplayName(
		"GIVEN README template exists AND README contains properties " +
			"WHEN executing generateReadme task THEN generates README successfully",
	)
	fun `test generateReadme generates README with README template AND with properties`() {
		buildFile.appendText(
			"""
			readmeTemplate {
				properties.set(mapOf(
					"version" to "1.3.2",
					"items" to listOf("item1", "item2").joinToString("\n")
				))
			}
			""".trimIndent(),
		)
		readmeTemplate.writeText(getResource("generate/with-properties/README-template.md")!!.readText())

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("generateReadme")
				.forwardOutput()
				.build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":generateReadme")?.outcome)
		assertEquals(getResource("generate/with-properties/README-expected.md")!!.readText(), generatedReadme.readText())
	}

	@Test
	@DisplayName(
		"GIVEN README template exists AND no root README exists " +
			"WHEN executing updateReadme task THEN copies README file generated from generateReadme task",
	)
	fun `test updateReadme copies generated README with README template AND without root README`() {
		readmeTemplate.writeText(getResource("generate/no-properties/README-template.md")!!.readText())

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("updateReadme")
				.forwardOutput()
				.build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":generateReadme")?.outcome)
		assertEquals(TaskOutcome.SUCCESS, result.task(":updateReadme")?.outcome)
		assertEquals(generatedReadme.readText(), rootReadme.readText())
	}

	@Test
	@DisplayName(
		"GIVEN README template exists AND root README exists " +
			"WHEN executing updateReadme task THEN copies README file generated from generateReadme task",
	)
	fun `test updateReadme copies generated README with README template AND with root README`() {
		rootReadme.writeText(
			"""
			# Test Project
			
			 **Test project description**
			""".trimIndent(),
		)
		readmeTemplate.writeText(getResource("generate/no-properties/README-template.md")!!.readText())

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("updateReadme")
				.forwardOutput()
				.build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":generateReadme")?.outcome)
		assertEquals(TaskOutcome.SUCCESS, result.task(":updateReadme")?.outcome)
		assertEquals(generatedReadme.readText(), rootReadme.readText())
	}

	private fun getResource(name: String): URL? = this::class.java.classLoader.getResource(name)
}
