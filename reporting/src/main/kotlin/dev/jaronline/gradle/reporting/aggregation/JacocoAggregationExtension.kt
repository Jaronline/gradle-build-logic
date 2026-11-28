package dev.jaronline.gradle.reporting.aggregation

import org.gradle.api.provider.SetProperty

abstract class JacocoAggregationExtension {
	abstract val testSuites: SetProperty<String>

	init {
		testSuites.convention(setOf("test")).finalizeValueOnRead()
	}

	fun testSuite(suiteName: String) {
		testSuites.add(suiteName)
	}

	fun testSuites(vararg suiteNames: String) {
		testSuites.set(suiteNames.asIterable())
	}
}
