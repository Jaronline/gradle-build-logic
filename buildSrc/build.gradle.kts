plugins {
	`kotlin-dsl` apply false
}

repositories {
	gradlePluginPortal()
}

dependencies {
	subprojects.forEach(this::runtimeOnly)
}
