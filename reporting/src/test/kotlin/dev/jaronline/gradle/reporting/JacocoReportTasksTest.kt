package dev.jaronline.gradle.reporting

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.internal.tasks.DefaultSourceSetContainer
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.util.concurrent.Callable
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.gradle.api.tasks.testing.Test as TestTask

class JacocoReportTasksTest {
	lateinit var project: Project
	lateinit var test: TaskProvider<TestTask>
	lateinit var integrationTest: TaskProvider<TestTask>
	lateinit var mainSourceSet: SourceSet

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		test = project.tasks.register<TestTask>("test")
		integrationTest = project.tasks.register<TestTask>("integrationTest")
	}

	@Test
	fun `test jacocoReportTask in project without jacoco plugin throws GradleException`() {
		val exception =
			assertThrows<GradleException> {
				project.jacocoReportTask(integrationTest)
			}
		assertEquals(
			"jacocoReportTask(TaskProvider<Test>) can only be used with the jacoco plugin enabled!",
			exception.message,
		)
	}

	@Test
	fun `test jacocoReportTask in project without sourceSets extension throws GradleException`() {
		project.pluginManager.apply(JacocoPlugin::class.java)

		val exception =
			assertThrows<GradleException> {
				project.jacocoReportTask(integrationTest)
			}
		assertEquals(
			"jacocoReportTask(TaskProvider<Test>) can only be used with the sourceSets extension!",
			exception.message,
		)
	}

	@Test
	fun `test jacocoReportTask(TaskProvider) with report that already exists throws GradleException`() {
		project.applyRequirements()
		project.defaultJacocoReportTask()

		val exception =
			assertThrows<GradleException> {
				project.jacocoReportTask(test)
			}
		assertEquals(
			"""
			A jacoco report task with the name jacocoTestReport already exists!
			You can register a jacoco report task with a custom name by using jacocoReportTask(String, vararg TaskProvider<Test>)
			""".trimIndent(),
			exception.message,
		)
	}

	@Test
	fun `test jacocoReportTask(TaskProvider) without existing report does not throw`() {
		project.applyRequirements()

		assertDoesNotThrow {
			project.jacocoReportTask(test)
		}
	}

	@Test
	fun `test jacocoReportTask(TaskProvider) without existing report registers report correctly`() {
		project.applyRequirements()
		val jacoco = project.the<JacocoPluginExtension>()

		val reportTask = project.jacocoReportTask(test).get()
		assertEquals("verification", reportTask.group)
		assertEquals("Generates code coverage report for the test task.", reportTask.description)

		assertEquals(1, reportTask.dependsOn.size)
		assertEquals(test, reportTask.dependsOn.elementAt(0))

		assertEquals(1, reportTask.executionData.from.size)
		assertInstanceOf<ConfigurableFileCollection>(reportTask.executionData.from.elementAt(0))

		assertEquals(1, reportTask.sourceDirectories.from.size)
		val sourceDirectoryElement = reportTask.sourceDirectories.from.elementAt(0)
		assertInstanceOf<Callable<Set<File>>>(sourceDirectoryElement)
		val sourceDirectorySet = sourceDirectoryElement.call()
		assertEquals(mainSourceSet.allJava.srcDirs.size, sourceDirectorySet.size)

		assertEquals(1, reportTask.classDirectories.from.size)
		assertEquals(mainSourceSet.output, reportTask.classDirectories.from.elementAt(0))

		assertEquals(
			"${jacoco.reportsDirectory.get()}/test/html",
			reportTask.reports.html.outputLocation.asFile
				.get()
				.path,
		)
	}

	@Test
	fun `test jacocoReportTask(String, TaskCollection) with report that already exists throws GradleException`() {
		project.applyRequirements()
		project.defaultJacocoReportTask()

		val exception =
			assertThrows<GradleException> {
				project.jacocoReportTask("jacocoTestReport", project.tasks.withType<TestTask>())
			}
		assertEquals(
			"A jacoco report task with the name jacocoTestReport already exists!",
			exception.message,
		)
	}

	@Test
	fun `test jacocoReportTask(String, TaskCollection) without existing report does not throw`() {
		project.applyRequirements()

		assertDoesNotThrow {
			project.jacocoReportTask("jacocoTestReport", project.tasks.withType<TestTask>())
		}
	}

	@Test
	fun `test jacocoReportTask(String, TaskCollection) without existing report registers report correctly`() {
		project.applyRequirements()
		val jacoco = project.the<JacocoPluginExtension>()
		val testTasks = project.tasks.withType<TestTask>()

		val reportTask = project.jacocoReportTask("jacocoTestReport", testTasks).get()
		assertEquals("verification", reportTask.group)
		assertEquals("Generates the jacocoTestReport code coverage report.", reportTask.description)

		assertEquals(1, reportTask.dependsOn.size)
		assertEquals(testTasks, reportTask.dependsOn.elementAt(0))

		assertEquals(1, reportTask.executionData.from.size)
		assertInstanceOf<ConfigurableFileCollection>(reportTask.executionData.from.elementAt(0))

		assertEquals(1, reportTask.sourceDirectories.from.size)
		val sourceDirectoryElement = reportTask.sourceDirectories.from.elementAt(0)
		assertInstanceOf<Callable<Set<File>>>(sourceDirectoryElement)
		val sourceDirectorySet = sourceDirectoryElement.call()
		assertEquals(mainSourceSet.allJava.srcDirs.size, sourceDirectorySet.size)

		assertEquals(1, reportTask.classDirectories.from.size)
		assertEquals(mainSourceSet.output, reportTask.classDirectories.from.elementAt(0))

		assertEquals(
			"${jacoco.reportsDirectory.get()}/jacocoTestReport/html",
			reportTask.reports.html.outputLocation.asFile
				.get()
				.path,
		)
	}

	@Test
	fun `test jacocoReportTask(String, vararg TaskProvider) with report that already exists throws GradleException`() {
		project.applyRequirements()
		project.defaultJacocoReportTask()

		val exception =
			assertThrows<GradleException> {
				project.jacocoReportTask("jacocoTestReport", test, integrationTest)
			}
		assertEquals(
			"A jacoco report task with the name jacocoTestReport already exists!",
			exception.message,
		)
	}

	@Test
	fun `test jacocoReportTask(String, vararg TaskProvider) without existing report does not throw`() {
		project.applyRequirements()

		assertDoesNotThrow {
			project.jacocoReportTask("jacocoTestReport", test, integrationTest)
		}
	}

	@RepeatedTest(5, failureThreshold = 1)
	fun `test jacocoReportTask(String, vararg TaskProvider) without existing report registers report correctly`() {
		project.applyRequirements()
		val jacoco = project.the<JacocoPluginExtension>()

		val reportTask = project.jacocoReportTask("jacocoTestReport", test, integrationTest).get()
		assertEquals("verification", reportTask.group)
		assertEquals("Generates the jacocoTestReport code coverage report.", reportTask.description)

		assertEquals(2, reportTask.dependsOn.size)
		assertTrue(reportTask.dependsOn.contains(test))
		assertTrue(reportTask.dependsOn.contains(integrationTest))

		assertEquals(1, reportTask.executionData.from.size)
		assertInstanceOf<ConfigurableFileCollection>(reportTask.executionData.from.elementAt(0))

		assertEquals(1, reportTask.sourceDirectories.from.size)
		val sourceDirectoryElement = reportTask.sourceDirectories.from.elementAt(0)
		assertInstanceOf<Callable<Set<File>>>(sourceDirectoryElement)
		val sourceDirectorySet = sourceDirectoryElement.call()
		assertEquals(mainSourceSet.allJava.srcDirs.size, sourceDirectorySet.size)

		assertEquals(1, reportTask.classDirectories.from.size)
		assertEquals(mainSourceSet.output, reportTask.classDirectories.from.elementAt(0))

		assertEquals(
			"${jacoco.reportsDirectory.get()}/jacocoTestReport/html",
			reportTask.reports.html.outputLocation.asFile
				.get()
				.path,
		)
	}

	@Nested
	inner class TestCoverageDataActionTest {
		@Test
		fun `test execute adds jacoco destination file to testCoverageData`() {
			project.applyRequirements()

			val testCoverageData = project.files()
			val destinationFile = test.map { it.the<JacocoTaskExtension>().destinationFile }
			val action = TestCoverageDataAction(testCoverageData)

			action.execute(test.get())

			assertEquals(1, testCoverageData.from.size)
			val from = testCoverageData.from.elementAt(0)
			assertInstanceOf<Provider<File>>(from)
			assertEquals(destinationFile.get(), from.get())
		}
	}

	private fun Project.applyRequirements() {
		pluginManager.apply(JacocoPlugin::class.java)
		extensions.add("sourceSets", objects.newInstance<DefaultSourceSetContainer>())
		extensions.configure(
			"sourceSets",
			Action<SourceSetContainer> {
				mainSourceSet = register("main").get()
			},
		)
	}

	private fun Project.defaultJacocoReportTask() {
		tasks.register<JacocoReport>("jacocoTestReport")
	}
}
