plugins {
	id("dev.jaronline.build-properties")
	`kotlin-dsl`
	id("dev.jaronline.jar")
	id("org.jetbrains.dokka")
	id("dev.jaronline.test")
	id("dev.jaronline.report.jacoco")
}

group = "${buildProperties.groupId.get()}.kotlin"

repositories {
	gradlePluginPortal()
}

dependencies {
	implementation(project(":common"))
	implementation(kotlin("gradle-plugin", libs.versions.kotlin.get()))
	implementation(libs.plugin.dokka)
	testImplementation(gradleApi())
	testImplementation(gradleTestKit())
	testImplementation(kotlin("test", libs.versions.kotlin.get()))
	testImplementation(libs.junit.jupiter.params)
}

gradlePlugin {
	plugins {
		register("kotlin") {
			id = "dev.jaronline.project.kotlin"
			implementationClass = "dev.jaronline.gradle.kotlin.KotlinPlugin"
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