package dev.jaronline.gradle.common

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.create

class JaronlinePlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.extensions.create<JaronlineExtension>("jaronline")
	}
}

abstract class JaronlineExtension {
	abstract val javaVersion: Property<JavaLanguageVersion>
}
