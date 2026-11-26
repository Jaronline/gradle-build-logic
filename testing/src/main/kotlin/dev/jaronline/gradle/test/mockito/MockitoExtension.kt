package dev.jaronline.gradle.test.mockito

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

internal const val DEFAULT_MOCKITO_VERSION = "5.24.0"

abstract class MockitoExtension {
	abstract val version: Property<String>

	init {
		version.convention(DEFAULT_MOCKITO_VERSION)
	}

	fun library(artifact: String): Provider<Any> = version.map { mockitoLibrary(artifact, it) }
}
