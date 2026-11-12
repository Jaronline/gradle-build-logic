package dev.jaronline.gradle.publish

import org.gradle.api.Project
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPom
import org.gradle.kotlin.dsl.newInstance
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.Test
import kotlin.test.assertEquals

class MavenPomExtensionsTest {
	lateinit var project: Project
	lateinit var pom: DefaultMavenPom

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		pom = project.objects.newInstance<DefaultMavenPom>()
	}

	@Test
	fun `WHEN calling MavenPomLicenseSpec#mitLicense() THEN adds MIT license correctly`() {
		pom.licenses {
			mitLicense()
		}

		assertEquals(1, pom.licenses.size)
		val license = pom.licenses[0]
		assertEquals("MIT License", license.name.orNull)
		assertEquals("https://opensource.org/license/mit/", license.url.orNull)
	}

	@Test
	fun `WHEN calling MavenPomLicenseSpec#mitLicense(action) THEN adds MIT license correctly`() {
		pom.licenses {
			mitLicense {
				distribution.set("repo")
			}
		}

		assertEquals(1, pom.licenses.size)
		val license = pom.licenses[0]
		assertEquals("MIT License", license.name.orNull)
		assertEquals("https://opensource.org/license/mit/", license.url.orNull)
		assertEquals("repo", license.distribution.orNull)
	}

	@Test
	fun `WHEN calling MavenPomDeveloperSpec#jaronlineDeveloper() THEN adds developer with correct properties`() {
		pom.developers {
			jaronlineDeveloper()
		}

		assertEquals(1, pom.developers.size)
		val developer = pom.developers[0]
		assertEquals("jaronline", developer.id.orNull)
		assertEquals("Jaron Zijlstra", developer.name.orNull)
		assertEquals("https://github.com/JaronZ", developer.url.orNull)
		assertEquals("info@jaronline.dev", developer.email.orNull)
		assertEquals("Jaronline", developer.organization.orNull)
		assertEquals("https://github.com/Jaronline", developer.organizationUrl.orNull)
	}

	@ParameterizedTest(name = "GIVEN repository is {0} WHEN calling MavenPom#gitScm(repository) THEN sets correct scm properties")
	@MethodSource("gitRepositories")
	fun `GIVEN repository is not empty WHEN calling MavenPom#gitScm(repository) THEN sets correct scm properties`(repository: String) {
		pom.gitScm(project.provider { repository })

		assertEquals("https://$repository/", pom.scm.url.orNull)
		assertEquals("scm:git:git://$repository.git", pom.scm.connection.orNull)
		assertEquals("scm:git:ssh://git@$repository.git", pom.scm.developerConnection.orNull)
	}

	companion object {
		@JvmStatic
		fun gitRepositories(): List<Arguments> =
			listOf(
				Arguments.of("github.com/Jaronline/gradle-build-logic"),
				Arguments.of("gitlab.com/JaronZ/TestRepo"),
				Arguments.of("bitbucket.com/JaronZ/AnotherTestRepo"),
			)
	}
}
