package dev.jaronline.gradle.project

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.DocsType
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.language.jvm.tasks.ProcessResources

class SubprojectDependenciesPlugin : Plugin<Project> {
	override fun apply(project: Project): Unit =
		project.run {
			val dependencyClasses =
				configurations.create("dependencyClasses") {
					isCanBeConsumed = false
					isCanBeResolved = true
					attributes {
						attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
						attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
						attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.CLASSES))
					}
				}

			val dependencyResources =
				configurations.create("dependencyResources") {
					isCanBeConsumed = false
					isCanBeResolved = true
					attributes {
						attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
						attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
						attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.RESOURCES))
					}
				}

			val dependencySources =
				configurations.create("dependencySources") {
					isCanBeConsumed = false
					isCanBeResolved = true
					attributes {
						attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
						attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
						attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
					}
				}

			if (tasks.findByName("jar") != null) {
				tasks.named<Jar>("jar") {
					from(dependencyClasses)
					from(dependencyResources)
				}
			}

			if (tasks.findByName("processResources") != null) {
				tasks.named<ProcessResources>("processResources") {
					from(dependencyResources)
				}
			}

			val unpackDependencySources =
				tasks.register<UnpackArchives>("unpackDependencySources") {
					archives.from(dependencySources)
					excludedPatterns.add("META-INF/MANIFEST.MF")
					outputDirectory.set(layout.buildDirectory.dir("generated/dependencySources"))
				}

			if (tasks.findByName("sourcesJar") != null) {
				tasks.named<Jar>("sourcesJar") {
					from(unpackDependencySources)
				}
			}
		}
}
