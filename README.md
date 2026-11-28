<div align="center">

# Gradle Build Logic

**Gradle composite build logic used across multiple Jaronline projects.**

[![GitHub](https://img.shields.io/github/license/jaronline/gradle-build-logic)](https://github.com/jaronline/gradle-build-logic/blob/main/LICENSE)
[![Version](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.jaronline.dev%2Freleases%2Fdev%2Fjaronline%2Fgradle%2Fgradle-build-logic%2Fmaven-metadata.xml&strategy=highestVersion&label=version)](https://maven.jaronline.dev/#/releases/dev/jaronline/gradle/gradle-build-logic)

</div>

## Usage

Add the Jaronline Maven repository to plugin resolution:

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        maven("https://maven.jaronline.dev/releases/")
        // Optional fallback repository for gradle-build-logic
        maven {
            url = uri("https://maven.pkg.github.com/Jaronline/gradle-build-logic")
            credentials {
                username = "<your_github_username>"
                password = "<your_github_token>"
            }
        }
    }
}
```

The GitHub Packages repository requires credentials. See [Using a published package](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package) for more information.

Then apply one or more of the plugins to your project:

```kotlin
// build.gradle.kts
plugins {
    id("dev.jaronline.<plugin>") version "0.12.0"
}
```

## Plugins

- `dev.jaronline.formatting`
- `dev.jaronline.project.kotlin`
- `dev.jaronline.project.readme-template`
- `dev.jaronline.project.subproject-dependencies`
- `dev.jaronline.publish`
- `dev.jaronline.reporting.jacoco.aggregation`
- `dev.jaronline.test.mockito`
