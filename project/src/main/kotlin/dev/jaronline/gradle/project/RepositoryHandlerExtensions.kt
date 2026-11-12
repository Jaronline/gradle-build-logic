package dev.jaronline.gradle.project

import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.jaronline(repository: String): MavenArtifactRepository =
	maven("https://maven.jaronline.dev/$repository") {
		name = "jaronline"
	}

fun RepositoryHandler.jaronline(
	repository: String,
	action: Action<MavenArtifactRepository>,
): MavenArtifactRepository {
	val repo = jaronline(repository)
	action.execute(repo)
	return repo
}

fun RepositoryHandler.githubPackages(
	owner: String,
	repository: String,
	action: Action<MavenArtifactRepository>,
): MavenArtifactRepository =
	maven("https://maven.pkg.github.com/$owner/$repository") {
		name = "githubPackages"
		action.execute(this)
	}
