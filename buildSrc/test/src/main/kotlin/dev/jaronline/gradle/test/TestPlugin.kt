package dev.jaronline.gradle.test

import dev.jaronline.gradle.BuildPropertiesExtension
import dev.jaronline.gradle.BuildPropertiesPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.the

class TestPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configureTest()
	}
}

private fun Project.configureTest() {
	pluginManager.apply(BuildPropertiesPlugin::class.java)

	val buildProperties = the<BuildPropertiesExtension>()

	dependencies.add("testImplementation", buildProperties.libraryLookup("junit.jupiter.api").get())
    dependencies.add("testRuntimeOnly", buildProperties.libraryLookup("junit.jupiter.engine").get())
    dependencies.add("testRuntimeOnly", buildProperties.libraryLookup("junit.platform.launcher").get())

	tasks.named<Test>("test") {
		useJUnitPlatform()
		testLogging {
			events = setOf(TestLogEvent.FAILED)
			exceptionFormat = TestExceptionFormat.FULL
		}
	}
}