package dev.jaronline.gradle.test

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.junit(
	group: String,
	artifact: String,
	version: String? = null,
): Any {
	val dependency = "org.junit.$group:junit-$artifact"
	if (version != null) {
		return "$dependency:$version"
	}
	return dependency
}

fun DependencyHandler.junitJupiter(
	artifact: String,
	version: String? = null,
): Any = junit("jupiter", "jupiter-$artifact", version)

fun DependencyHandler.junitPlatform(
	artifact: String,
	version: String? = null,
): Any = junit("platform", "platform-$artifact", version)
