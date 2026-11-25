import dev.jaronline.gradle.formatting.baseJaronlineRules
import dev.jaronline.gradle.project.githubPackages
import dev.jaronline.gradle.project.jaronline
import dev.jaronline.gradle.publish.gitScm
import dev.jaronline.gradle.publish.jaronlineDeveloper
import dev.jaronline.gradle.publish.mitLicense

plugins {
	id("dev.jaronline.project")
	alias(libs.plugins.jaronline.subproject.dependencies)
	alias(libs.plugins.jaronline.readme.template)
	alias(libs.plugins.java.formatting)
	alias(libs.plugins.jaronline.formatting)
	id("dev.jaronline.report.jacoco.aggregation")
	`kotlin-dsl`
	alias(libs.plugins.dokka)
	id("dev.jaronline.jar")
	alias(libs.plugins.jaronline.publish)
	`java-gradle-plugin`
}

testing {
	@Suppress("UnstableApiUsage")
	suites {
		val smokeTest = register<JvmTestSuite>("smokeTest")

		named<JvmTestSuite>("test") {
			targets {
				all {
					testTask.configure {
						shouldRunAfter(smokeTest)
					}
				}
			}
		}
	}
}

sourceSets {
	main {
		java.setSrcDirs(listOf<String>())
		kotlin.setSrcDirs(listOf<String>())
	}
	test {
		java.setSrcDirs(listOf<String>())
		kotlin.setSrcDirs(listOf<String>())
		resources.setSrcDirs(listOf<String>())
	}
}

val smokeTest =
	sourceSets.named("smokeTest") {
		java.setSrcDirs(listOf<String>())
		resources.setSrcDirs(listOf<String>())
	}

tasks.named<Test>("smokeTest") {
	systemProperty("publishedPluginVersion", project.version.toString())
	dependsOn(tasks.publishToMavenLocal)
}

dependencies {
	jacocoAggregation(project(":kotlin"))
	subprojects.forEach {
		dependencyClasses(project(it.path)) {
			isTransitive = false
		}
		dependencyResources(project(it.path)) {
			isTransitive = false
		}
		dependencySources(project(it.path)) {
			isTransitive = false
		}
		dokka(project(it.path))
	}
	api(libs.plugin.spotless)
	api(kotlin("gradle-plugin", libs.versions.kotlin.get()))
	api(libs.plugin.dokka)
	api(libs.plugin.maven.publish)

	"smokeTestImplementation"(libs.junit.jupiter.api)
	"smokeTestImplementation"(kotlin("test", libs.versions.kotlin.get()))
	"smokeTestRuntimeOnly"(libs.junit.jupiter.engine)
	"smokeTestRuntimeOnly"(libs.junit.platform.launcher)
}

gradlePlugin {
	testSourceSets(smokeTest.get())
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks.jar {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.processResources {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val sourcesJar =
	tasks.named<Jar>("sourcesJar") {
		exclude("**/Readme.md")
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
		archiveClassifier.set("sources")
	}

val javadocJar =
	tasks.named<Jar>("javadocJar") {
		from(tasks.named("dokkaGeneratePublicationHtml"))
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
		archiveClassifier.set("javadoc")
	}

spotless {
	ratchetFrom = "origin/main"

	kotlinGradle {
		target(
			"*.gradle.kts",
			"buildSrc/*.gradle.kts",
			"buildSrc/*/*.gradle.kts",
			*subprojects.map { "${it.projectDir.path}/*.gradle.kts" }.toTypedArray(),
		)
		baseJaronlineRules()
	}

	kotlin {
		target("*/src/*/kotlin/dev/jaronline/**/*.kt")
		baseJaronlineRules()
	}
}

repositories {
	mavenCentral()
}

publishing {
	repositories {
		jaronline("releases") {
			credentials(PasswordCredentials::class.java)
		}

		githubPackages("Jaronline", "gradle-build-logic") {
			credentials {
				username = buildProperties.githubActor
				password = buildProperties.githubToken
			}
		}
	}
}

mavenPublishing {
	publishToMavenCentral()

	pom {
		name = buildProperties.projectName
		description = buildProperties.projectDescription
		inceptionYear = buildProperties.projectInceptionYear
		url = buildProperties.githubUrl.map { "https://$it/" }
		licenses {
			mitLicense {
				distribution = "repo"
			}
		}
		developers {
			jaronlineDeveloper()
		}
		gitScm(buildProperties.githubUrl)
	}
}

afterEvaluate {
	publishing {
		publications {
			named<MavenPublication>("pluginMaven") {
				setArtifacts(listOf<Any>())
				artifact(tasks.jar)
				artifact(sourcesJar)
				artifact(javadocJar)
			}
		}
	}
}

allprojects {
	plugins.withId("java-gradle-plugin") {
		gradlePlugin {
			if (path != ":") {
				isAutomatedPublishing = false
			}

			plugins {
				if (listOf(":", ":formatting").contains(path)) {
					register("formatting") {
						id = "dev.jaronline.formatting"
						implementationClass = "dev.jaronline.gradle.formatting.FormattingPlugin"
					}
				}

				if (listOf(":", ":kotlin").contains(path)) {
					register("kotlin") {
						id = "dev.jaronline.project.kotlin"
						implementationClass = "dev.jaronline.gradle.kotlin.KotlinPlugin"
					}
				}

				if (listOf(":", ":project").contains(path)) {
					register("subprojectDependencies") {
						id = "dev.jaronline.project.subproject-dependencies"
						implementationClass = "dev.jaronline.gradle.project.SubprojectDependenciesPlugin"
					}
					register("readmeTemplate") {
						id = "dev.jaronline.project.readme-template"
						implementationClass = "dev.jaronline.gradle.project.readme.ReadmeTemplatePlugin"
					}
				}

				if (listOf(":", ":publish").contains(path)) {
					register("publish") {
						id = "dev.jaronline.publish"
						implementationClass = "dev.jaronline.gradle.publish.PublishPlugin"
					}
				}
			}
		}
	}
}

readmeTemplate {
	properties =
		mapOf(
			"version" to spotlessChangelog.versionLast,
			"pluginsList" to gradlePlugin.plugins.sortedBy { it.id }.joinToString("\n") { "- `${it.id}`" },
		)
}
