package dev.jaronline.gradle.kotlin

import dev.jaronline.gradle.BuildPropertiesExtension
import dev.jaronline.gradle.BuildPropertiesPlugin
import org.gradle.api.JavaVersion
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
	pluginManager.apply(BuildPropertiesPlugin::class.java)
	val buildProperties = the<BuildPropertiesExtension>()

	pluginManager.apply(KotlinPluginWrapper::class.java)
	pluginManager.apply(DokkaPlugin::class.java)

	extensions.configure<KotlinJvmProjectExtension> {
		jvmToolchain(JavaVersion.toVersion(buildProperties.javaVersion.version).majorVersion.toInt())
	}
}