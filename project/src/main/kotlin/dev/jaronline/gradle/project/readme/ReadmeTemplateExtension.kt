package dev.jaronline.gradle.project.readme

import org.gradle.api.provider.MapProperty

abstract class ReadmeTemplateExtension {
	abstract val properties: MapProperty<String, Any>
}
