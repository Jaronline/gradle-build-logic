package dev.jaronline.gradle.test.mockito

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.mockito(
	artifact: String,
	version: String? = null,
): Any = mockitoLibrary(artifact, version)

internal fun mockitoLibrary(
	artifact: String,
	version: String?,
): Any {
	val dependency = "org.mockito:mockito-$artifact"
	if (version != null) {
		return "$dependency:$version"
	}
	return dependency
}
