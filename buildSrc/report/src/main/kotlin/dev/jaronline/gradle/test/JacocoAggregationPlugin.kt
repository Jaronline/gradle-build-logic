package dev.jaronline.gradle.test

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.reporting.ReportingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.testing.jacoco.plugins.JacocoCoverageReport
import org.gradle.testing.jacoco.plugins.JacocoReportAggregationPlugin

class JacocoAggregationPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configureJacocoAggregation()
	}
}

private fun Project.configureJacocoAggregation() {
	@Suppress("UnstableApiUsage")
	pluginManager.apply(JacocoReportAggregationPlugin::class.java)

	extensions.configure<ReportingExtension> {
		@Suppress("UnstableApiUsage")
		reports {
			named<JacocoCoverageReport>("testCodeCoverageReport") {
				testSuiteName.set("test")
			}
		}
	}

	val configureTestTask: Task.() -> Unit = {
		dependsOn(subprojects.mapNotNull { it.tasks.findByName("test") })
		finalizedBy(tasks.named("testCodeCoverageReport"))
	}

	if (tasks.findByName("test") == null)
		tasks.register("test", configureTestTask)
	else
		tasks.named("test", configureTestTask)
}