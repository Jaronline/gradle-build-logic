import dev.jaronline.gradle.test.junitJupiter
import dev.jaronline.gradle.test.mockito.useMockitoAgent

plugins {
	id("dev.jaronline.build-properties")
	`kotlin-dsl`
	id("dev.jaronline.jar")
	id("org.jetbrains.dokka")
	alias(libs.plugins.jaronline.mockito)
	id("dev.jaronline.report.jacoco")
}

group = "${buildProperties.groupId.get()}.publish"

repositories {
	gradlePluginPortal()
}

dependencies {
	implementation(libs.plugin.maven.publish)
	testImplementation(junitJupiter("params", libs.versions.junit.get()))
	testImplementation(mockito.library("junit-jupiter"))
}

sourceSets {
	configureEach {
		java.setSrcDirs(listOf<String>())
		resources.setSrcDirs(listOf<String>())
	}
}

tasks.test {
	useMockitoAgent()
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks.named<Jar>("javadocJar") {
	from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
	archiveClassifier.set("javadoc")
}