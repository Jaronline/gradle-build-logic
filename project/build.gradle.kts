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

group = "${buildProperties.groupId.get()}.project"

repositories {
	gradlePluginPortal()
}

testing {
	@Suppress("UnstableApiUsage")
	suites {
		val test = named<JvmTestSuite>("test") {
			dependencies {
				implementation(junitJupiter("params"))
				implementation(mockito.library("junit-jupiter").get())
			}

			targets {
				all {
					testTask.configure {
						useMockitoAgent()
					}
				}
			}
		}

		val integrationTest = register<JvmTestSuite>("integrationTest") {
			dependencies {
				implementation(project())
				implementation(gradleApi())
			}

			targets {
				all {
					testTask.configure {
						shouldRunAfter(test)
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

sourceSets {
	named("main") {
		resources.setSrcDirs(listOf<String>())
	}
	named("test") {
		resources.setSrcDirs(listOf<String>())
	}
	named("integrationTest") {
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