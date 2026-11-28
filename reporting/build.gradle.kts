import dev.jaronline.gradle.test.junitJupiter

plugins {
	id("dev.jaronline.build-properties")
	`kotlin-dsl`
	id("dev.jaronline.jar")
	id("org.jetbrains.dokka")
	id("dev.jaronline.report.jacoco")
}

group = "${buildProperties.groupId.get()}.reporting"

repositories {
	gradlePluginPortal()
	mavenCentral()
}

testing {
	@Suppress("UnstableApiUsage")
	suites {
		val test = named<JvmTestSuite>("test") {
			dependencies {
				implementation(junitJupiter("params", libs.versions.junit.get()))
			}
		}

		register<JvmTestSuite>("functionalTest") {
			targets {
				all {
					testTask.configure {
						shouldRunAfter(test)
					}
				}
			}
		}
	}
}

sourceSets {
	configureEach {
		java.setSrcDirs(listOf<String>())
		resources.setSrcDirs(listOf<String>())
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