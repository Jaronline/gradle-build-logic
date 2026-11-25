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
	@Suppress("AvoidDuplicateDependencies")
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
		jacocoAggregation(project(it.path))
		dokka(project(it.path))
	}
	api(libs.plugin.spotless)
	api(libs.plugin.java.formatting)
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
	exclusiveContent {
		forRepository { maven("https://maven.blamejared.com/") }
		filter {
			includeGroup("net.mezzdev.java-formatting")
			includeModule("net.mezzdev.gradle", "JavaFormatting")
		}
	}
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
				mapOf<String, Runnable>(
					":formatting" to
						Runnable {
							register("formatting") {
								id = "dev.jaronline.formatting"
								implementationClass = "dev.jaronline.gradle.formatting.FormattingPlugin"
							}
						},
					":kotlin" to
						Runnable {
							register("kotlin") {
								id = "dev.jaronline.project.kotlin"
								implementationClass = "dev.jaronline.gradle.kotlin.KotlinPlugin"
							}
						},
					":project" to
						Runnable {
							register("subprojectDependencies") {
								id = "dev.jaronline.project.subproject-dependencies"
								implementationClass = "dev.jaronline.gradle.project.SubprojectDependenciesPlugin"
							}
							register("readmeTemplate") {
								id = "dev.jaronline.project.readme-template"
								implementationClass = "dev.jaronline.gradle.project.readme.ReadmeTemplatePlugin"
							}
						},
					":publish" to
						Runnable {
							register("publish") {
								id = "dev.jaronline.publish"
								implementationClass = "dev.jaronline.gradle.publish.PublishPlugin"
							}
						},
				).filter { listOf(":", it.key).contains(path) }.values.forEach(Runnable::run)
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
