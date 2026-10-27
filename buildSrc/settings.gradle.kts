dependencyResolutionManagement {
	versionCatalogs {
		register("libs") {
			from(files("../gradle/libs.versions.toml"))
		}
	}
}

rootProject.name = "buildSrc"
include(
	"build-properties",
	"project",
	"kotlin",
	"test",
	"report",
)
