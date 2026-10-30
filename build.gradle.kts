import dev.jaronline.gradle.UnpackArchives

plugins {
	id("dev.jaronline.project")
	id("dev.jaronline.kotlin.formatting")
	id("dev.jaronline.report.jacoco.aggregation")
	`kotlin-dsl` apply false
	id("org.jetbrains.dokka")
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

tasks.named<Jar>("sourcesJar") {
	from(unpackDependencySources)
	exclude("**/Readme.md")
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	archiveClassifier.set("sources")
}

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

		ktlint()
		leadingSpacesToTabs()
		endWithNewline()
	}
}

repositories {
	mavenCentral()
}
