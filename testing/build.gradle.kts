plugins {
	id("dev.jaronline.build-properties")
	`kotlin-dsl`
	id("dev.jaronline.jar")
	id("org.jetbrains.dokka")
	id("dev.jaronline.test")
	id("dev.jaronline.report.jacoco")
}

group = "${buildProperties.groupId.get()}.test"

repositories {
	gradlePluginPortal()
	mavenCentral()
}

dependencies {
	testImplementation(kotlin("test", libs.versions.kotlin.get()))
}

sourceSets {
	named("main") {
		java.setSrcDirs(listOf<String>())
		resources.setSrcDirs(listOf<String>())
	}
	named("test") {
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