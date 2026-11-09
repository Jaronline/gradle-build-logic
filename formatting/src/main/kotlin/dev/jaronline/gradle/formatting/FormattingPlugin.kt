package dev.jaronline.gradle.formatting

import com.diffplug.gradle.spotless.KotlinGradleExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class FormattingPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.pluginManager.apply(SpotlessPlugin::class.java)
	}
}

fun KotlinGradleExtension.baseJaronlineRules() {
	ktlint()
	leadingSpacesToTabs()
	endWithNewline()
}
