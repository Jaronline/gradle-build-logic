package dev.jaronline.gradle.formatting

import com.diffplug.gradle.spotless.BaseKotlinExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class FormattingPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.pluginManager.apply(SpotlessPlugin::class.java)
	}
}

fun BaseKotlinExtension.baseJaronlineRules() {
	ktlint()
	leadingSpacesToTabs()
	endWithNewline()
}
