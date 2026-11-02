package dev.jaronline.gradle.formatting

import org.gradle.api.provider.Property

abstract class FormattingExtension {
	abstract val ratchetFrom: Property<String>
}
