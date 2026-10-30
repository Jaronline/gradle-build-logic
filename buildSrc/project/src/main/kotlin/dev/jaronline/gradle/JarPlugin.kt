package dev.jaronline.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

class JarPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configureJar()
	}
}

private fun Project.configureJar() {
	pluginManager.apply(BuildPropertiesPlugin::class.java)
	val buildProperties = the<BuildPropertiesExtension>()

	tasks.withType<Jar>().configureEach {
		manifest {
			attributes(mapOf(
				"Specification-Title" to project.name,
				"Specification-Vendor" to buildProperties.vendor.get(),
				"Specification-Version" to rootProject.version,
				"Implementation-Title" to name,
				"Implementation-Vendor" to buildProperties.vendor.get(),
				"Implementation-Version" to rootProject.version
			))
		}
	}
}