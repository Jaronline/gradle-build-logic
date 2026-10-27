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
		register("test") {
			id = "dev.jaronline.test"
			implementationClass = "dev.jaronline.gradle.test.TestPlugin"
		}
		register("mockito") {
			id = "dev.jaronline.test.mockito"
			implementationClass = "dev.jaronline.gradle.test.MockitoPlugin"
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
