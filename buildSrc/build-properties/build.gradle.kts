plugins {
	`kotlin-dsl`
}

repositories {
	gradlePluginPortal()
}

gradlePlugin {
	plugins {
		register("buildProperties") {
			id = "dev.jaronline.build-properties"
			implementationClass = "dev.jaronline.gradle.BuildPropertiesPlugin"
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
