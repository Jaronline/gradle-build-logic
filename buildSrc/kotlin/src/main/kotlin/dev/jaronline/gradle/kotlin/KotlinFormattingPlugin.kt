package dev.jaronline.gradle.kotlin

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class KotlinFormattingPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configureKotlinFormatting()
	}
}

private fun Project.configureKotlinFormatting() {
	pluginManager.apply(SpotlessPlugin::class.java)

	extensions.configure<SpotlessExtension> {
		kotlin {
			target("*/src/*/kotlin/dev/jaronline/**/*.kt")
			ktlint()
			leadingSpacesToTabs()
			endWithNewline()
			replaceRegex("class-level kdoc indentation fix", "^\\*", " *")
			replaceRegex("method-level kdoc indentation fix", "\t\\*", "\t *")
		}
	}
}