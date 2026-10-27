package dev.jaronline.gradle.test

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.named
import org.gradle.testing.jacoco.plugins.JacocoPlugin

class JacocoReportPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configureJacocoReporting()
	}
}

private fun Project.configureJacocoReporting() {
	pluginManager.apply(JacocoPlugin::class.java)

	tasks.named<Test>("test") {
		finalizedBy(tasks.named("jacocoTestReport"))
	}
}