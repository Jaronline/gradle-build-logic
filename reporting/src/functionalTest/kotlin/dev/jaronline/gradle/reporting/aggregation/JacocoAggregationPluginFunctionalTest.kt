package dev.jaronline.gradle.reporting.aggregation

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals

class JacocoAggregationPluginFunctionalTest {
	@TempDir
	lateinit var projectDir: File

	private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }
	private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }

	@Test
	@DisplayName(
		"GIVEN jacoco aggregation has custom test suites WHEN applying jacoco aggregation plugin " +
			"THEN creates reports from custom test suites",
	)
	fun `test aggregation creates correct reports`() {
		settingsFile.writeText("")
		buildFile.writeText(
			$$"""
			plugins {
				id("dev.jaronline.reporting.jacoco.aggregation")
			}
			
			val suites = arrayOf("smokeTest", "test", "integrationTest", "functionalTest")
			
			jacoco {
				aggregation {
					testSuites(*suites)
				}
			}
			
			tasks.register("checkReports") {
				doLast {
					val hasCorrectReportsSize = reporting.reports.toList().size == suites.size
					check(hasCorrectReportsSize) { "Expected to find ${suites.size} reports" }
					for (suite in suites) {
						val reportName = "${suite}CodeCoverageReport"
						val report = reporting.reports.findByName(reportName)
						checkNotNull(report) { "Expected report $reportName to exist" }
						check(report is JacocoCoverageReport) { "Expected report $reportName to be JacocoCoverageReport" }
						val hasCorrectTestSuiteName = report.testSuiteName.orNull == suite
						check(hasCorrectTestSuiteName) { "Expected testSuiteName of report $reportName to be $suite" }
					}
				}
			}
			""".trimIndent(),
		)

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("checkReports")
				.forwardOutput()
				.build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":checkReports")?.outcome)
	}
}
