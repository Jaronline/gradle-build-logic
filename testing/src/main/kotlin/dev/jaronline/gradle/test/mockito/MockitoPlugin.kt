package dev.jaronline.gradle.test.mockito

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.create

class MockitoPlugin : Plugin<Project> {
	override fun apply(project: Project): Unit =
		project.run {
			val extension = extensions.create<MockitoExtension>("mockito")

			val mockitoAgent =
				configurations.create("mockitoAgent") {
					isCanBeConsumed = false
					isCanBeResolved = true
					dependencies.configureEach {
						(this as? ExternalModuleDependency)?.isTransitive = false
					}
				}

			dependencies.add(
				mockitoAgent.name,
				extension.version.map { version -> dependencies.mockito("core", version) },
			)
		}
}
