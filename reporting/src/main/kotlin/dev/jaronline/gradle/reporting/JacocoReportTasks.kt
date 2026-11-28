package dev.jaronline.gradle.reporting

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

fun Project.jacocoReportTask(testTask: TaskProvider<Test>): TaskProvider<JacocoReport> {
	checkRequirements("jacocoReportTask(TaskProvider<Test>)")

	val reportName = "jacoco${testTask.name.capitalized()}Report"

	if (tasks.findByName(reportName) != null) {
		throw GradleException(
			"""
			A jacoco report task with the name $reportName already exists!
			You can register a jacoco report task with a custom name by using jacocoReportTask(String, vararg TaskProvider<Test>)
			""".trimIndent(),
		)
	}

	val jacoco = the<JacocoPluginExtension>()
	val sourceSets = the<SourceSetContainer>()

	val testCoverageData = files()
	testTask.configure(TestCoverageDataAction(testCoverageData))

	return tasks.register<JacocoReport>(reportName) {
		group = "verification"
		description = "Generates code coverage report for the ${testTask.name} task."

		dependsOn(testTask)
		executionData(testCoverageData)
		sourceSets(sourceSets.named("main").get())

		reports {
			html.outputLocation.set(file("${jacoco.reportsDirectory.get()}/${testTask.name}/html"))
		}
	}
}

fun Project.jacocoReportTask(
	name: String,
	testTasks: TaskCollection<Test>,
): TaskProvider<JacocoReport> {
	checkRequirements("jacocoReportTask(String, TaskCollection<Test>)")

	if (tasks.findByName(name) != null) {
		throw GradleException("A jacoco report task with the name $name already exists!")
	}

	val sourceSets = the<SourceSetContainer>()

	val testCoverageData = files()
	testTasks.configureEach(TestCoverageDataAction(testCoverageData))

	return tasks.register<JacocoReport>(name) {
		group = "verification"
		description = "Generates the $name code coverage report."

		dependsOn(testTasks)
		executionData(testCoverageData)
		sourceSets(sourceSets.named("main").get())
	}
}

fun Project.jacocoReportTask(
	name: String,
	vararg testTasks: TaskProvider<Test>,
): TaskProvider<JacocoReport> {
	checkRequirements("jacocoReportTask(String, vararg TaskProvider<Test>)")

	if (tasks.findByName(name) != null) {
		throw GradleException("A jacoco report task with the name $name already exists!")
	}

	val sourceSets = the<SourceSetContainer>()

	val testCoverageData = files()
	testTasks.forEach {
		it.configure(TestCoverageDataAction(testCoverageData))
	}

	return tasks.register<JacocoReport>(name) {
		group = "verification"
		description = "Generates the $name code coverage report."

		dependsOn(*testTasks)
		executionData(testCoverageData)
		sourceSets(sourceSets.named("main").get())
	}
}

internal class TestCoverageDataAction(
	private val testCoverageData: ConfigurableFileCollection,
) : Action<Test> {
	override fun execute(task: Test) {
		val jacocoExt = task.extensions.getByType<JacocoTaskExtension>()
		testCoverageData.from(task.project.provider { jacocoExt.destinationFile })
	}
}

private fun Project.checkRequirements(method: String) {
	if (!pluginManager.hasPlugin("jacoco")) {
		throw GradleException("$method can only be used with the jacoco plugin enabled!")
	}
	if (extensions.findByType<SourceSetContainer>() == null) {
		throw GradleException("$method can only be used with the sourceSets extension!")
	}
}
