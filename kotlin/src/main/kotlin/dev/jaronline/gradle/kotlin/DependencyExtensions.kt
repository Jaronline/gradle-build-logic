package dev.jaronline.gradle.kotlin

import org.gradle.api.plugins.jvm.JvmComponentDependencies

@Suppress("UnstableApiUsage")
fun JvmComponentDependencies.kotlin(
	module: String,
	version: String? = null,
): String = "org.jetbrains.kotlin:kotlin-$module${version?.let { ":$it" } ?: ""}"
