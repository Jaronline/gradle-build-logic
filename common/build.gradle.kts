plugins {
	`kotlin-dsl`
}

repositories {
	gradlePluginPortal()
}

gradlePlugin {
	plugins {
		register("base") {
			id = "dev.jaronline.project.base"
			implementationClass = "dev.jaronline.gradle.common.BasePlugin"
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