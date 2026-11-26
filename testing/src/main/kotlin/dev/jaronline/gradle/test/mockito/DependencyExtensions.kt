package dev.jaronline.gradle.test.mockito

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.jvm.JvmComponentDependencies

fun DependencyHandler.mockito(
	artifact: String,
	version: String? = null,
): String = mockitoLibrary(artifact, version)

@Suppress("UnstableApiUsage")
fun JvmComponentDependencies.mockito(
	artifact: String,
	version: String? = null,
): String = mockitoLibrary(artifact, version)

internal fun mockitoLibrary(
	artifact: String,
	version: String?,
): String = "org.mockito:mockito-$artifact${version?.let { ":$it" } ?: ""}"
