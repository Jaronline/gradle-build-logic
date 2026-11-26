package dev.jaronline.gradle.test

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.junit(
	group: String,
	artifact: String,
	version: String? = null,
): String = "org.junit.$group:junit-$artifact${version?.let { ":$it" } ?: ""}"

fun DependencyHandler.junitJupiter(
	artifact: String,
	version: String? = null,
): String = junit("jupiter", "jupiter-$artifact", version)

fun DependencyHandler.junitPlatform(
	artifact: String,
	version: String? = null,
): String = junit("platform", "platform-$artifact", version)
