package dev.jaronline.gradle.test

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.jvm.JvmComponentDependencies

fun DependencyHandler.junit(
	group: String,
	artifact: String,
	version: String? = null,
): String = junitLibrary(group, artifact, version)

fun DependencyHandler.junitJupiter(
	artifact: String,
	version: String? = null,
): String = junit("jupiter", "jupiter-$artifact", version)

fun DependencyHandler.junitPlatform(
	artifact: String,
	version: String? = null,
): String = junit("platform", "platform-$artifact", version)

@Suppress("UnstableApiUsage")
fun JvmComponentDependencies.junit(
	group: String,
	artifact: String,
	version: String? = null,
): String = junitLibrary(group, artifact, version)

@Suppress("UnstableApiUsage")
fun JvmComponentDependencies.junitJupiter(
	artifact: String,
	version: String? = null,
): String = junit("jupiter", "jupiter-$artifact", version)

@Suppress("UnstableApiUsage")
fun JvmComponentDependencies.junitPlatform(
	artifact: String,
	version: String? = null,
): String = junit("platform", "platform-$artifact", version)

internal fun junitLibrary(
	group: String,
	artifact: String,
	version: String?,
): String = "org.junit.$group:junit-$artifact${version?.let { ":$it" } ?: ""}"
