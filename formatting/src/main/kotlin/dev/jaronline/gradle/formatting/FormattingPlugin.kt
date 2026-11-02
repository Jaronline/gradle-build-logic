package dev.jaronline.gradle.formatting

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import dev.jaronline.gradle.common.JaronlineExtension
import dev.jaronline.gradle.common.JaronlinePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.the

class FormattingPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configureFormatting()
	}
}

private fun Project.configureFormatting() {
	pluginManager.apply(JaronlinePlugin::class.java)
	pluginManager.apply(SpotlessPlugin::class.java)

	val jaronline = the<JaronlineExtension>() as ExtensionAware
	val formatting = jaronline.extensions.create<FormattingExtension>("formatting")

	extensions.configure<SpotlessExtension> {
		ratchetFrom = formatting.ratchetFrom.orNull
	}
}
