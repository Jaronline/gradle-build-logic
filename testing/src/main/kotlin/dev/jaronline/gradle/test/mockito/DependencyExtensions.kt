package dev.jaronline.gradle.test.mockito

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.mockito(
	artifact: String,
	version: String? = null,
): Any = mockitoLibrary(artifact, version)

internal fun mockitoLibrary(
	artifact: String,
	version: String?,
): Any = "org.mockito:mockito-$artifact${version?.let { ":$it" } ?: ""}"
