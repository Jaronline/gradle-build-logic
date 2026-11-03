package dev.jaronline.gradle.formatting

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Action
import org.gradle.api.provider.Property

abstract class FormattingExtension(
	private val spotless: SpotlessExtension,
) {
	abstract val ratchetFrom: Property<String>

	fun kotlinGradle(closure: Action<KotlinGradleExtension>) {
		spotless.kotlinGradle {
			closure.execute(KotlinGradleExtension(this))
		}
	}
}
