package dev.jaronline.gradle.kotlin

import dev.jaronline.gradle.common.JaronlineExtension
import dev.jaronline.gradle.common.JaronlinePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.the
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class KotlinPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configureKotlinProject()
	}
}

private fun Project.configureKotlinProject() {
	pluginManager.apply(JaronlinePlugin::class.java)
	pluginManager.apply(KotlinPluginWrapper::class.java)
	pluginManager.apply(DokkaPlugin::class.java)

	val jaronline = the<JaronlineExtension>()
	val kotlin = (jaronline as ExtensionAware).extensions.create<KotlinExtension>("kotlin")

	extensions.configure<KotlinJvmProjectExtension> {
		jvmToolchain {
			languageVersion.set(kotlin.javaVersion)
		}
	}
}

abstract class KotlinExtension {
	abstract val javaVersion: Property<JavaLanguageVersion>
}
