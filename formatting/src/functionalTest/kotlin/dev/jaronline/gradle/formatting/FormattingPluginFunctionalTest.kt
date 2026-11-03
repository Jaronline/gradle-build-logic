package dev.jaronline.gradle.formatting

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.net.URL

class FormattingPluginFunctionalTest {
	@field:TempDir
	lateinit var projectDir: File

	private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }
	private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }

	@BeforeEach
	fun setup() {
		settingsFile.writeText("")
		buildFile.writeText(
			"""
			plugins {
				id("dev.jaronline.formatting")
			}
			
			repositories {
				gradlePluginPortal()
			}
			
			""".trimIndent(),
		)
	}

	@Test
	fun `GIVEN correctly formatted kotlinGradle file WHEN checking with default rules THEN task is successful`() {
		buildFile.appendText(
			"""
			jaronline {
				formatting {
					kotlinGradle {
						target("test.gradle.kts")
						baseRules()
					}
				}
			}
			""".trimIndent(),
		)

		val testFile = projectDir.resolve("test.gradle.kts")
		testFile.writeText(getResource("correct-formatting.gradle.kts")!!.readText())

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("spotlessKotlinGradleCheck")
				.forwardOutput()
				.build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":spotlessKotlinGradleCheck")?.outcome)
	}

	@Test
	fun `GIVEN incorrectly formatted kotlinGradle file WHEN checking with default rules THEN task is failure`() {
		buildFile.appendText(
			"""
			jaronline {
				formatting {
					kotlinGradle {
						target("test.gradle.kts")
						baseRules()
					}
				}
			}
			""".trimIndent(),
		)

		val testFile = projectDir.resolve("test.gradle.kts")
		testFile.writeText(getResource("incorrect-formatting.gradle.kts")!!.readText())

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("spotlessKotlinGradleCheck")
				.forwardOutput()
				.buildAndFail()

		val output = result.output.replace(" ", "")
		assertTrue(
			output.contains(
				"""
				-plugins{
				+plugins·{
				\t`java`
				-·`idea`
				+\t`idea`
				\t`eclipse`
				}
				""".trimIndent(),
			),
		)
		assertTrue(
			output.contains(
				"""
				idea·{
				-module·{
				+\tmodule·{
				\t\tisDownloadSources·=·true
				-\tisDownloadJavadoc·=·false
				+\t\tisDownloadJavadoc·=·false
				\t\texcludeDirs.addAll(files("build",·"out",·"logs"))
				\t}
				}
				""".trimIndent(),
			),
		)
		assertEquals(TaskOutcome.FAILED, result.task(":spotlessKotlinGradleCheck")?.outcome)
	}

	private fun getResource(name: String): URL? = this::class.java.classLoader.getResource(name)
}
