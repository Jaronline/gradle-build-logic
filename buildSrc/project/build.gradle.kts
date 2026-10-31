plugins {
	`kotlin-dsl`
}

repositories {
	gradlePluginPortal()
}

dependencies {
	implementation(project(":build-properties"))
	implementation(libs.plugin.changelog)
	implementation(libs.plugin.spotless.changelog)
}

gradlePlugin {
	plugins {
		register("changelog") {
			id = "dev.jaronline.changelog"
			implementationClass = "dev.jaronline.gradle.ChangelogPlugin"
		}
		register("project") {
			id = "dev.jaronline.project"
			implementationClass = "dev.jaronline.gradle.ProjectPlugin"
		}
		register("jar") {
			id = "dev.jaronline.jar"
			implementationClass = "dev.jaronline.gradle.JarPlugin"
		}
		register("publish") {
			id = "dev.jaronline.publish"
			implementationClass = "dev.jaronline.gradle.PublishPlugin"
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
