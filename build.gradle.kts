import dev.jaronline.gradle.UnpackArchives
import dev.jaronline.gradle.formatting.baseJaronlineRules
import dev.jaronline.gradle.project.githubPackages
import dev.jaronline.gradle.project.jaronline
import dev.jaronline.gradle.publish.gitScm
import dev.jaronline.gradle.publish.jaronlineDeveloper
import dev.jaronline.gradle.publish.mit

plugins {
	id("dev.jaronline.project")
	alias(libs.plugins.jaronline.formatting)
	id("dev.jaronline.kotlin.formatting")
	id("dev.jaronline.report.jacoco.aggregation")
	`kotlin-dsl` apply false
	id("org.jetbrains.dokka")
	id("dev.jaronline.jar")
	alias(libs.plugins.maven.publish)
	alias(libs.plugins.jaronline.publish)
	`java-gradle-plugin`
}

val dependencyClasses =
	configurations.create("dependencyClasses") {
		isCanBeConsumed = false
		isCanBeResolved = true
		attributes {
			attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
			attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
			attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.CLASSES))
		}
	}

val dependencyResources =
	configurations.create("dependencyResources") {
		isCanBeConsumed = false
		isCanBeResolved = true
		attributes {
			attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
			attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
			attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.RESOURCES))
		}
	}

val dependencySources =
	configurations.create("dependencySources") {
		isCanBeConsumed = false
		isCanBeResolved = true
		attributes {
			attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
			attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
			attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
		}
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
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks.jar {
	from(dependencyClasses)
	from(dependencyResources)
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.processResources {
	from(dependencyResources)
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

@Suppress("TaskMissingDescription")
val unpackDependencySources =
	tasks.register<UnpackArchives>("unpackDependencySources") {
		archives.from(dependencySources)
		excludedPatterns.add("META-INF/MANIFEST.MF")
		outputDirectory.set(layout.buildDirectory.dir("generated/dependencySources"))
	}

val sourcesJar =
	tasks.named<Jar>("sourcesJar") {
		from(unpackDependencySources)
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
			license {
				mit()
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
				if (listOf(":", ":common").contains(path)) {
					register("base") {
						id = "dev.jaronline.project.base"
						implementationClass = "dev.jaronline.gradle.common.BasePlugin"
					}
				}

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
