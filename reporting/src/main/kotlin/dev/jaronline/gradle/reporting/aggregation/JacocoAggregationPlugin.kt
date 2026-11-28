package dev.jaronline.gradle.reporting.aggregation

import org.gradle.api.Action
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.reporting.ReportSpec
import org.gradle.api.reporting.ReportingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.testing.jacoco.plugins.JacocoCoverageReport
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoReportAggregationPlugin

@Suppress("UnstableApiUsage")
class JacocoAggregationPlugin : Plugin<Project> {
	override fun apply(project: Project): Unit =
		project.run {
			pluginManager.apply(JacocoReportAggregationPlugin::class.java)

			val jacoco = the<JacocoPluginExtension>() as ExtensionAware
			val extension = jacoco.extensions.create<JacocoAggregationExtension>("aggregation")
			afterEvaluate(TestSuiteReportsAction(extension))
		}

	internal class TestSuiteReportsAction(
		private val extension: JacocoAggregationExtension,
	) : Action<Project> {
		override fun execute(project: Project) {
			project.extensions.configure<ReportingExtension> {
				reports {
					for (suite in extension.testSuites.get()) {
						findOrRegister<JacocoCoverageReport>("${suite}CodeCoverageReport") {
							testSuiteName.set(suite)
						}
					}
				}
			}
		}

		private inline fun <reified T : ReportSpec> ExtensiblePolymorphicDomainObjectContainer<in T>.findOrRegister(
			name: String,
			noinline configuration: T.() -> Unit,
		): NamedDomainObjectProvider<T> {
			if (name in names) {
				return named(name, configuration)
			}
			return register(name, configuration)
		}
	}
}
