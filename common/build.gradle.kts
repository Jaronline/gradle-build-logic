plugins {
	`kotlin-dsl`
	id("dev.jaronline.jar")
	id("org.jetbrains.dokka")
	id("jacoco")
}

repositories {
	gradlePluginPortal()
}

gradlePlugin {
	plugins {
		register("base") {
			id = "dev.jaronline.project.base"
			implementationClass = "dev.jaronline.gradle.common.BasePlugin"
		}
	}
}

sourceSets {
	named("main") {
		java.setSrcDirs(listOf<String>())
		resources.setSrcDirs(listOf<String>())
	}
	named("test") {
		java.setSrcDirs(listOf<String>())
		kotlin.setSrcDirs(listOf<String>())
		resources.setSrcDirs(listOf<String>())
	}
}

java {
	withSourcesJar()
}

@Suppress("TaskMissingDescription")
val javadocJar = tasks.register<Jar>("javadocJar") {
	from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
	archiveClassifier.set("javadoc")
}

tasks.build {
	dependsOn(javadocJar)
}