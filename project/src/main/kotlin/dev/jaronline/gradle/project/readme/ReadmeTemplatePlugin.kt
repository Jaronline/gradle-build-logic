package dev.jaronline.gradle.project.readme

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.language.jvm.tasks.ProcessResources

class ReadmeTemplatePlugin : Plugin<Project> {
	override fun apply(project: Project): Unit =
		project.run {
			val extension = extensions.create<ReadmeTemplateExtension>("readmeTemplate")

			val generatedReadmeDirectory = layout.buildDirectory.dir("generated/readme")
			val generatedReadmeFile = generatedReadmeDirectory.map { it.file("README.md") }

			val generateReadme =
				tasks.register<ProcessResources>("generateReadme") {
					description = "Generate the README.md file"

					from(layout.projectDirectory.file("src/main/resources/README.md"))
					into(generatedReadmeDirectory)

					val properties = extension.properties.orNull
					if (properties != null) {
						inputs.properties(properties)
						expand(properties)
					}
				}

			tasks.register<Copy>("updateReadme") {
				description = "Updates the root README.md file"

				dependsOn(generateReadme)

				from(generatedReadmeFile)
				into(layout.projectDirectory)
			}

			val readmeCheck =
				tasks.register<ReadmeCheck>("readmeCheck") {
					dependsOn(generateReadme)

					rootReadme.set(layout.projectDirectory.file("README.md"))
					generatedReadme.set(generatedReadmeFile)
				}

			if (tasks.findByName("check") != null) {
				tasks.named("check") {
					dependsOn(readmeCheck)
				}
			} else {
				tasks.register("check") {
					dependsOn(readmeCheck)
				}
			}
		}
}
