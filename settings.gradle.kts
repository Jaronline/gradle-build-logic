pluginManagement {
	repositories {
		exclusiveContent {
			forRepository { maven("https://maven.blamejared.com/") }
			filter {
				includeGroup("net.mezzdev.java-formatting")
				includeModule("net.mezzdev.gradle", "JavaFormatting")
			}
		}
		exclusiveContent {
			forRepository { maven("https://maven.jaronline.dev/releases/") }
			filter {
				@Suppress("UnstableApiUsage")
				includeGroupAndSubgroups("dev.jaronline")
			}
		}
		gradlePluginPortal()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

runCatching {
	logger.info("Configuring git hooks")

	providers
		.exec {
			commandLine("git", "config", "--local", "core.hooksPath", ".githooks")
		}.result
		.get()

	logger.info("Git hooks configured")
}.onFailure {
	logger.error("Could not auto-configure Git hooks: ${it.message}")
}

rootProject.name = "gradle-build-logic"
include(
	"common",
	"kotlin",
	"formatting",
	"publish",
	"project",
)
