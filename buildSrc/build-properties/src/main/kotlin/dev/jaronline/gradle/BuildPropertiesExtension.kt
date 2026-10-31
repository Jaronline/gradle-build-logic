package dev.jaronline.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.the

abstract class BuildPropertiesExtension(private val project: Project) {
    val libs = findVersionCatalog("libs")

    val vendor = gradleProperty("vendor")
	val groupId = gradleProperty("group_id")
	val githubUrl = gradleProperty("github_url")

    val projectName = gradleProperty("name")
    val projectDescription = gradleProperty("description")
    val projectInceptionYear = gradleProperty("inception_year")
    val projectLicense = gradleProperty("license")
    val projectLicenseUrl = gradleProperty("license_url")
    val projectLicenseDistribution = gradleProperty("license_distribution")
    val developerId = gradleProperty("developer_id")
    val developerName = gradleProperty("developer_name")
    val developerUrl = gradleProperty("developer_url")
    val developerEmail = gradleProperty("developer_email")
    val developerOrganization = gradleProperty("developer_organization")
    val developerOrganizationUrl = gradleProperty("developer_organization_url")

    val projectVersion = gradleProperty("version")

	val javaVersion = versionLookup("java")

    val githubActor = environmentVariable("GITHUB_ACTOR").orNull
    val githubToken = environmentVariable("GITHUB_TOKEN").orNull

	fun gradleProperty(propertyName: String): Provider<String> {
		return project.providers.gradleProperty(propertyName)
	}

    fun environmentVariable(variableName: String): Provider<String> {
        return project.providers.environmentVariable(variableName)
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