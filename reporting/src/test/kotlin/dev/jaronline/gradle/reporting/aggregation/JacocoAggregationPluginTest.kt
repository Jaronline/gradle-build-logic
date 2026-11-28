package dev.jaronline.gradle.reporting.aggregation

import org.gradle.api.Project
import org.gradle.api.reporting.ReportingExtension
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.plugins.JacocoCoverageReport
import org.gradle.testing.jacoco.plugins.JacocoReportAggregationPlugin
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertNotNull
import kotlin.test.assertEquals

@Suppress("UnstableApiUsage")
class JacocoAggregationPluginTest {
	lateinit var project: Project

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
	}

	@Test
	fun `test plugin applies correct plugins and extensions to project`() {
		assertDoesNotThrow {
			project.pluginManager.apply(JacocoAggregationPlugin::class.java)
		}
		assertNotNull(project.plugins.findPlugin(JacocoReportAggregationPlugin::class.java))
		assertNotNull(project.extensions.findByType<ReportingExtension>())
	}

	@Nested
	inner class TestSuiteReportsActionTest {
		lateinit var extension: JacocoAggregationExtension
		internal lateinit var action: JacocoAggregationPlugin.TestSuiteReportsAction
		lateinit var reporting: ReportingExtension

		@BeforeEach
		fun setup() {
			assertDoesNotThrow {
				project.pluginManager.apply(JacocoReportAggregationPlugin::class.java)
			}
			extension = project.extensions.create<JacocoAggregationExtension>("aggregation")
			action = JacocoAggregationPlugin.TestSuiteReportsAction(extension)
			reporting = project.the<ReportingExtension>()
		}

		@Test
		fun `test execute configures test report only with extension with default values`() {
			action.execute(project)
			assertEquals(1, reporting.reports.toList().size)
			assertNotNull(reporting.reports.findByName("testCodeCoverageReport"))
			val report = reporting.reports.named("testCodeCoverageReport").get()
			assertInstanceOf<JacocoCoverageReport>(report)
			assertEquals("test", report.testSuiteName.orNull)
		}

		@Test
		fun `test execute configures multiple reports with custom testSuites`() {
			val suites = arrayOf("integrationTest", "functionalTest")
			extension.testSuites(*suites)
			action.execute(project)

			assertEquals(suites.size, reporting.reports.toList().size)
			suites.forEach {
				assertNotNull(reporting.reports.findByName("${it}CodeCoverageReport"))
				val report = reporting.reports.named("${it}CodeCoverageReport").get()
				assertInstanceOf<JacocoCoverageReport>(report)
				assertEquals(it, report.testSuiteName.orNull)
			}
		}

		@Test
		fun `test execute configures existing report`() {
			val testReport = reporting.reports.register<JacocoCoverageReport>("testCodeCoverageReport").get()
			action.execute(project)

			assertEquals(1, reporting.reports.toList().size)
			assertEquals(testReport, reporting.reports.findByName("testCodeCoverageReport"))
			assertEquals("test", testReport.testSuiteName.orNull)
		}
	}
}
