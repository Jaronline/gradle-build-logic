package dev.jaronline.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals

class PublishedPluginSmokeTest {
	@TempDir
	lateinit var projectDir: File

	@Test
	fun `resolves plugin marker and transitive dependencies from mavenLocal`() {
		val pluginVersion = System.getProperty("publishedPluginVersion")
			?: error("Missing publishedPluginVersion system property")

		projectDir.resolve("settings.gradle.kts").writeText(
			"""
			pluginManagement {
				repositories {
					mavenLocal()
					gradlePluginPortal()
	                mavenCentral()
				}
			}
			
			dependencyResolutionManagement {
                repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
                repositories {
                    mavenLocal()
                    mavenCentral()
                    gradlePluginPortal()
                }
            }
			
            rootProject.name = "consumer-smoke-test"
			""".trimIndent()
		)

		projectDir.resolve("build.gradle.kts").writeText(
            """
            plugins {
                id("dev.jaronline.publish") version "$pluginVersion"
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
            .withArguments("smokeCheck", "--stacktrace", "--refresh-dependencies")
            .build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":smokeCheck")?.outcome)
	}
}