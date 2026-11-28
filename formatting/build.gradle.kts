import dev.jaronline.gradle.project.blameJared

plugins {
	id("dev.jaronline.project")
	`kotlin-dsl`
	id("dev.jaronline.jar")
	id("org.jetbrains.dokka")
	id("dev.jaronline.report.jacoco")
}

group = "${buildProperties.groupId.get()}.formatting"

repositories {
	gradlePluginPortal()
	exclusiveContent {
		forRepository { blameJared() }
		filter {
			includeGroup("net.mezzdev.java-formatting")
			includeModule("net.mezzdev.gradle", "JavaFormatting")
		}
	}
	mavenCentral()
}

testing {
	@Suppress("UnstableApiUsage")
	suites {
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
	implementation(libs.plugin.spotless)
	implementation(libs.plugin.java.formatting)
}

sourceSets {
	named("main") {
		resources.setSrcDirs(listOf<String>())
	}
	named("test") {
		resources.setSrcDirs(listOf<String>())
	}
	configureEach {
		java.setSrcDirs(listOf<String>())
	}
}

gradlePlugin {
	testSourceSets(sourceSets.named("functionalTest").get())
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks.named<Jar>("javadocJar") {
	from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
	archiveClassifier.set("javadoc")
}