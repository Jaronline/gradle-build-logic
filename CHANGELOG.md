# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/2.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- RepositoryHandler extension methods for the BlameJared Maven repository

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [0.9.0] - 2026-11-25

### Fixed

- Include `net.mezzdev.java-formatting` in pom dependencies

## [0.8.0] - 2026-11-25

### Added

- Kotlin formatting rules for the `dev.jaronline.formatting` plugin
- Java formatting with the `net.mezzdev.java-formatting` plugin

## [0.7.0] - 2026-11-24

### Added

- README template plugin

### Changed

- Rename `dev.jaronline.project.subproject.dependencies` plugin to `dev.jaronline.project.subproject-dependencies`

### Removed

- Base Jaronline plugin (`dev.jaronline.project.base`)

## [0.6.0] - 2026-11-23

### Added

- Subproject dependencies plugin

## [0.5.0] - 2026-11-23

### Added

- Dependencies are now being generated in the .module and pom.xml file
- MavenPomLicenseSpec.mitLicense methods as replacement to MavenPomLicense.mit()

### Changed

- Visibility modifier of MavenPomLicense.mit() is now private

## [0.4.1] - 2026-11-12

### Fixed

- Gradle plugin markers now get generated

## [0.4.0] - 2026-11-12

### Added

- RepositoryHandler extension methods for Jaronline and GitHub Packages Maven repositories

## [0.3.0] - 2026-11-10

### Added

- Publishing plugin with com.vanniktech.maven.publish plugin

## [0.2.0] - 2026-11-09

### Added

- Formatting plugin with spotless

### Removed

- Kotlin extension for kotlin plugin

## [0.1.0] - 2026-10-31

### Added

- Kotlin project plugin
- Base plugin for shared properties and extensions

[Unreleased]: https://github.com/Jaronline/gradle-build-logic/compare/v0.9.0...HEAD
[0.9.0]: https://github.com/Jaronline/gradle-build-logic/compare/v0.8.0...v0.9.0
[0.8.0]: https://github.com/Jaronline/gradle-build-logic/compare/v0.7.0...v0.8.0
[0.7.0]: https://github.com/Jaronline/gradle-build-logic/compare/v0.6.0...v0.7.0
[0.6.0]: https://github.com/Jaronline/gradle-build-logic/compare/v0.5.0...v0.6.0
[0.5.0]: https://github.com/Jaronline/gradle-build-logic/compare/v0.4.1...v0.5.0
[0.4.1]: https://github.com/Jaronline/gradle-build-logic/compare/v0.4.0...v0.4.1
[0.4.0]: https://github.com/Jaronline/gradle-build-logic/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/Jaronline/gradle-build-logic/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/Jaronline/gradle-build-logic/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/Jaronline/gradle-build-logic/commits/v0.1.0
