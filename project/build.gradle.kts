plugins {
	id("dev.jaronline.build-properties")
	`kotlin-dsl`
	id("dev.jaronline.jar")
	id("org.jetbrains.dokka")
	id("dev.jaronline.test")
	id("dev.jaronline.test.mockito")
	id("dev.jaronline.report.jacoco")
}

group = "${buildProperties.groupId.get()}.project"

repositories {
	gradlePluginPortal()
}

testing {
	@Suppress("UnstableApiUsage")
	suites {
		val integrationTest = register<JvmTestSuite>("integrationTest") {
			dependencies {
				implementation(project())
				implementation(gradleApi())
			}

			targets {
				all {
					testTask.configure {
						shouldRunAfter(testing.suites.named("test"))
					}
				}
			}
		}

		register<JvmTestSuite>("functionalTest") {
			targets {
				all {
					testTask.configure {
						shouldRunAfter(integrationTest)
					}
				}
			}
		}
	}
}

dependencies {
	testImplementation(libs.junit.jupiter.params)
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
	named("integrationTest") {
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