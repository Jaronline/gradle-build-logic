plugins {
	id("dev.jaronline.project")
	id("dev.jaronline.kotlin.formatting")
	id("dev.jaronline.report.jacoco.aggregation")
	`kotlin-dsl` apply false
}

dependencies {
	jacocoAggregation(project(":kotlin"))
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
