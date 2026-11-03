package dev.jaronline.gradle.formatting

import com.diffplug.gradle.spotless.KotlinGradleExtension

class KotlinGradleExtension(
	private val kotlinGradle: KotlinGradleExtension,
) {
	fun target(vararg targets: Any) {
		kotlinGradle.target(*targets)
	}

	fun baseRules() {
		kotlinGradle.ktlint()
		kotlinGradle.leadingSpacesToTabs()
		kotlinGradle.endWithNewline()
	}
}
