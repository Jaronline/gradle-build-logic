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

testing {
	suites {
		@Suppress("UnstableApiUsage")
		register<JvmTestSuite>("functionalTest") {
			targets {
				all {
					testTask.configure {
						shouldRunAfter(testing.suites.named("test"))
					}
				}
			}
		}
	}
}

dependencies {
	implementation(project(":common"))
	implementation(libs.plugin.spotless)
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

val functionalTest = sourceSets.named("functionalTest") {
	java.setSrcDirs(listOf<String>())
}

gradlePlugin {
	testSourceSets(functionalTest.get())
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks.named<Jar>("javadocJar") {
	from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
	archiveClassifier.set("javadoc")
}