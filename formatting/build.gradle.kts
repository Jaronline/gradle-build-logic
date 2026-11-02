plugins {
	`kotlin-dsl`
	id("dev.jaronline.jar")
	id("org.jetbrains.dokka")
	id("dev.jaronline.test")
	id("dev.jaronline.report.jacoco")
}

repositories {
	gradlePluginPortal()
}

dependencies {
	implementation(project(":common"))
	implementation(libs.plugin.spotless)
	testImplementation(kotlin("test", libs.versions.kotlin.get()))
}

gradlePlugin {
	plugins {
		register("formatting") {
			id = "dev.jaronline.formatting"
			implementationClass = "dev.jaronline.gradle.formatting.FormattingPlugin"
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