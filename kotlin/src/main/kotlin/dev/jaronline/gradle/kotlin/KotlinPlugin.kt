package dev.jaronline.gradle.kotlin

import dev.jaronline.gradle.common.JaronlineExtension
import dev.jaronline.gradle.common.JaronlinePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
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

	extensions.configure<KotlinJvmProjectExtension> {
		jvmToolchain {
			languageVersion.set(jaronline.javaVersion)
		}
	}
}
