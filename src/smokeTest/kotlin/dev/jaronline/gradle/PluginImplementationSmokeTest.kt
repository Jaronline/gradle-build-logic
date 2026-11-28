package dev.jaronline.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals

class PluginImplementationSmokeTest {
	@TempDir
	lateinit var projectDir: File

	private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }
	private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }

	@ParameterizedTest(name = "test plugin {0} has existing implementationClass")
	@MethodSource("pluginIds")
	fun `test plugin implementationClass exists`(pluginId: String) {
		settingsFile.writeText("")
		buildFile.writeText(
			"""
			plugins {
				id("$pluginId")
			}
			
			tasks.register("smokeCheck") {
				doLast {
					println("Plugin applied successfully")
				}
			}
			""".trimIndent()
		)

		val result = GradleRunner.create()
			.withProjectDir(projectDir)
			.withPluginClasspath(pluginClasspath)
			.withArguments("smokeCheck", "--stacktrace")
			.build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":smokeCheck")?.outcome)
	}

	companion object {
		private lateinit var pluginClasspath: List<File>

		@JvmStatic
		@BeforeAll
		fun setupAll() {
			val paths = System.getProperty("pluginClasspath")
				?: error("Missing pluginClasspath system property")

			pluginClasspath = paths.split(File.pathSeparator)
				.filter { it.isNotBlank() }
				.map { File(it) }
		}

		@JvmStatic
		fun pluginIds(): List<Arguments> {
			val pluginIds = System.getProperty("pluginIds")
				?: error("Missing pluginIds system property")

			return pluginIds.split(File.pathSeparator).map {
				Arguments.of(it)
			}
		}
	}
}