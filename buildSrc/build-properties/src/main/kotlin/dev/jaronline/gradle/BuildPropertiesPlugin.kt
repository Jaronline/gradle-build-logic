package dev.jaronline.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class BuildPropertiesPlugin : Plugin<Project> {
	override fun apply(project: Project): Unit = project.run {
		extensions.create<BuildPropertiesExtension>("buildProperties", project)
	}
}
