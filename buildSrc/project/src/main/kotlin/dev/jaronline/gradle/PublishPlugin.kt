package dev.jaronline.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the

class PublishPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configurePublishing()
	}
}

private fun Project.configurePublishing() {
	pluginManager.apply(BuildPropertiesPlugin::class.java)
	pluginManager.apply(MavenPublishPlugin::class.java)

	val buildProperties = the<BuildPropertiesExtension>()

	extensions.configure<PublishingExtension> {
		repositories {
			maven {
				name = "githubPackages"
				url = uri("https://maven.pkg.${buildProperties.githubUrl.get()}")
				credentials {
					username = buildProperties.githubActor
					password = buildProperties.githubToken
				}
			}
		}
	}
}