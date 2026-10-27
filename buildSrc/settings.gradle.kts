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
	// Remove this along with the kotlin directory if you don't need kotlin
	"kotlin",
)
