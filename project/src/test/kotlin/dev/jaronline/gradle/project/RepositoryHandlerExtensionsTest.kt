package dev.jaronline.gradle.project

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class RepositoryHandlerExtensionsTest {
	lateinit var project: Project
	val repositories: RepositoryHandler get() = project.repositories

	@BeforeTest
	fun setup() {
		project = ProjectBuilder.builder().build()
	}

	@ParameterizedTest(
		name = "GIVEN repository is {0} WHEN calling RepositoryHandler#jaronline(repository) THEN adds Jaronline repository correctly",
	)
	@MethodSource("jaronlineRepositories")
	fun `test Jaronline repository is added correctly`(repository: String) {
		repositories.jaronline(repository)

		assertEquals(1, repositories.size)
		assertInstanceOf<MavenArtifactRepository>(repositories[0])
		val resultRepository = repositories[0] as MavenArtifactRepository
		assertEquals("jaronline", resultRepository.name)
		assertEquals("https://maven.jaronline.dev/$repository", resultRepository.url.toString())
	}

	@ParameterizedTest(
		name =
			"GIVEN repository is {0} WHEN calling RepositoryHandler#jaronline(repository, action)" +
				"THEN adds Jaronline repository correctly",
	)
	@MethodSource("jaronlineRepositories")
	fun `test Jaronline repository with action is added correctly`(repository: String) {
		repositories.jaronline(repository) {
			credentials {
				username = "testUsername"
				password = "testPassword"
			}
		}

		assertEquals(1, repositories.size)
		assertInstanceOf<MavenArtifactRepository>(repositories[0])
		val resultRepository = repositories[0] as MavenArtifactRepository
		assertEquals("jaronline", resultRepository.name)
		assertEquals("https://maven.jaronline.dev/$repository", resultRepository.url.toString())
		assertEquals("testUsername", resultRepository.credentials.username)
		assertEquals("testPassword", resultRepository.credentials.password)
	}

	@ParameterizedTest(
		name =
			"GIVEN owner is {0} AND repository is {1}" +
				"WHEN calling RepositoryHandler#githubPackages(owner, repository, action) THEN adds GitHub Packages repository correctly",
	)
	@MethodSource("githubPackagesData")
	fun `test GitHub Packages repository with action is added correctly`(
		owner: String,
		repository: String,
	) {
		repositories.githubPackages(owner, repository) {
			credentials {
				username = "testUsername"
				password = "testPassword"
			}
		}

		assertEquals(1, repositories.size)
		assertInstanceOf<MavenArtifactRepository>(repositories[0])
		val resultRepository = repositories[0] as MavenArtifactRepository
		assertEquals("githubPackages", resultRepository.name)
		assertEquals("https://maven.pkg.github.com/$owner/$repository", resultRepository.url.toString())
		assertEquals("testUsername", resultRepository.credentials.username)
		assertEquals("testPassword", resultRepository.credentials.password)
	}

	companion object {
		@JvmStatic
		fun jaronlineRepositories(): List<Arguments> =
			listOf(
				Arguments.of("releases"),
				Arguments.of("snapshots"),
				Arguments.of("private"),
			)

		@JvmStatic
		fun githubPackagesData(): List<Arguments> =
			listOf(
				Arguments.of("Jaronline", "gradle-build-logic"),
				Arguments.of("TOUCHTHEDUCK", "TrialsExpanded"),
				Arguments.of("JaronZ", "JaronZ"),
				Arguments.of("Jaronline", ".github"),
				Arguments.of("JaronZ", ".github-private"),
			)
	}
}
