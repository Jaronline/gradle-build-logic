package dev.jaronline.gradle.kotlin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class KotlinPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.pluginManager.apply(KotlinPluginWrapper::class.java)
		project.pluginManager.apply(DokkaPlugin::class.java)
	}
}
