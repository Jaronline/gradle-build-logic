plugins {
	`kotlin-dsl`
}

repositories {
	gradlePluginPortal()
}

dependencies {
	implementation(project(":build-properties"))
}

gradlePlugin {
	plugins {
		register("jacoco") {
			id = "dev.jaronline.report.jacoco"
			implementationClass = "dev.jaronline.gradle.test.JacocoReportPlugin"
		}
		register("jacocoAggregation") {
			id = "dev.jaronline.report.jacoco.aggregation"
			implementationClass = "dev.jaronline.gradle.test.JacocoAggregationPlugin"
		}
	}
}

sourceSets {
	named("main") {
		java.setSrcDirs(listOf<String>())
		resources.setSrcDirs(listOf<String>())
	}
	named("test") {
		java.setSrcDirs(listOf<String>())
		kotlin.setSrcDirs(listOf<String>())
		resources.setSrcDirs(listOf<String>())
	}
}
