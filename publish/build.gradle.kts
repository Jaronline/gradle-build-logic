import dev.jaronline.gradle.test.junitJupiter
import dev.jaronline.gradle.test.mockito.useMockitoAgent

plugins {
	id("dev.jaronline.project")
	`kotlin-dsl`
	id("dev.jaronline.jar")
	id("org.jetbrains.dokka")
	id("dev.jaronline.report.jacoco")
}

group = "${buildProperties.groupId.get()}.publish"

repositories {
	gradlePluginPortal()
}

dependencies {
	testImplementation(junitJupiter("params", libs.versions.junit.get()))
}

sourceSets {
	configureEach {
		java.setSrcDirs(listOf<String>())
		resources.setSrcDirs(listOf<String>())
	}
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks.named<Jar>("javadocJar") {
	from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
	archiveClassifier.set("javadoc")
}