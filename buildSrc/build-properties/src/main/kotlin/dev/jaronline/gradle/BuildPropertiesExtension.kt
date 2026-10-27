package dev.jaronline.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.the

abstract class BuildPropertiesExtension(private val project: Project) {
    val libs = findVersionCatalog("libs")

	val groupId = gradleProperty("group_id")
	val githubUrl = gradleProperty("github_url")

	val javaVersion = versionLookup("java")
    val pitestJunit5Version = versionLookup("pitest.junit5")

	fun gradleProperty(propertyName: String): Provider<String> {
		return project.providers.gradleProperty(propertyName)
	}

    fun versionLookup(id: String): GeoVersionConstraint {
        return GeoVersionConstraint(project.provider {
            libs.findVersion(id)
                .orElseThrow {
                    NoSuchElementException(
                        "Version '${
                            id.replace(
                                '.',
                                '-'
                            )
                        }' does not exist in version catalogue '${libs.name}'!"
                    )
                }
        })
    }

    fun libraryLookup(id: String): Provider<MinimalExternalModuleDependency> {
        return libs.findLibrary(id)
            .orElseThrow {
                NoSuchElementException(
                    "Library '${
                        id.replace(
                            '.',
                            '-'
                        )
                    }' does not exist in version catalogue '${libs.name}'!"
                )
            }
    }

    fun findVersionCatalog(name: String): VersionCatalog {
        return project.the<VersionCatalogsExtension>().find(name).orElseThrow {
            NoSuchElementException("No version catalogue '$name' exists!")
        }
    }
}