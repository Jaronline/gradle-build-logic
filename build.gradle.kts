import dev.jaronline.gradle.UnpackArchives

plugins {
	id("dev.jaronline.project")
	id("dev.jaronline.kotlin.formatting")
	id("dev.jaronline.report.jacoco.aggregation")
	`kotlin-dsl` apply false
	id("org.jetbrains.dokka")
	id("dev.jaronline.jar")
	alias(libs.plugins.maven.publish)
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

		ktlint()
		leadingSpacesToTabs()
		endWithNewline()
	}
}

repositories {
	mavenCentral()
}

publishing {
	repositories {
		maven {
			name = "jaronline"
			url = uri("https://maven.jaronline.dev/releases")
			credentials(PasswordCredentials::class.java)
		}

		maven {
			name = "githubPackages"
			url = uri("https://maven.pkg.${buildProperties.githubUrl.get()}")
			credentials {
				username = buildProperties.githubActor
				password = buildProperties.githubToken
			}
		}
	}
}

mavenPublishing {
	publishToMavenCentral()
	signAllPublications()
	coordinates(group.toString(), base.archivesName.get(), version.toString())

	pom {
		name = buildProperties.projectName
		description = buildProperties.projectDescription
		inceptionYear = buildProperties.projectInceptionYear
		url = buildProperties.githubUrl.map { "https://$it/" }
		licenses {
			license {
				name = buildProperties.projectLicense
				url = buildProperties.projectLicenseUrl
				distribution = buildProperties.projectLicenseDistribution
			}
		}
		developers {
			developer {
				id = buildProperties.developerId
				name = buildProperties.developerName
				url = buildProperties.developerUrl
				email = buildProperties.developerEmail
				organization = buildProperties.developerOrganization
				organizationUrl = buildProperties.developerOrganizationUrl
			}
		}
		scm {
			url = buildProperties.githubUrl.map { "https://$it/" }
			connection = buildProperties.githubUrl.map { "scm:git:git://$it.git" }
			developerConnection = buildProperties.githubUrl.map { "scm:git:ssh://git@$it.git" }
		}
	}
}

afterEvaluate {
	publishing {
		publications {
			named<MavenPublication>("maven") {
				setArtifacts(listOf<Any>())
				artifact(tasks.jar)
				artifact(sourcesJar)
				artifact(javadocJar)
			}
		}
	}
}
