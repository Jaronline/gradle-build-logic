package dev.jaronline.gradle.publish

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType

class PublishPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configurePublishing()
	}
}

private fun Project.configurePublishing() {
	pluginManager.apply(MavenPublishPlugin::class.java)

	val base = extensions.findByType<BasePluginExtension>()

	extensions.configure<MavenPublishBaseExtension> {
		signAllPublications()
		coordinates(group.toString(), base?.archivesName?.orNull ?: projectDir.name, version.toString())
	}
}
