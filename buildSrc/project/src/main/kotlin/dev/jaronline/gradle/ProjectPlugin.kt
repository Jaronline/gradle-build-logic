package dev.jaronline.gradle

import com.diffplug.spotless.changelog.gradle.ChangelogExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel

class ProjectPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configureProject()
	}
}

private fun Project.configureProject() {
	pluginManager.apply(BuildPropertiesPlugin::class.java)
	pluginManager.apply(ChangelogPlugin::class.java)
    pluginManager.apply(JavaPlugin::class.java)
    pluginManager.apply(IdeaPlugin::class.java)
    pluginManager.apply(EclipsePlugin::class.java)

	val buildProperties = the<BuildPropertiesExtension>()
	val spotlessChangelog = the<ChangelogExtension>()

	group = buildProperties.groupId.get()
	version = spotlessChangelog.versionNext

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(buildProperties.javaVersion.version))
        }
    }

    extensions.configure<IdeaModel> {
        module {
            isDownloadSources = true
            isDownloadJavadoc = false
            excludeDirs.addAll(files("build", "out", "logs"))
        }
    }

    extensions.configure<EclipseModel> {
        classpath {
            isDownloadSources = true
            isDownloadJavadoc = false
        }
    }

    tasks.withType<Javadoc>().configureEach {
		// workaround cast for https://github.com/gradle/gradle/issues/7038
		val standardJavadocDocletOptions = options as StandardJavadocDocletOptions
		// prevent java 8's strict doclint for javadocs from failing builds
		standardJavadocDocletOptions.addStringOption("Xdoclint:none", "-quiet")
		// Omit bundled web fonts to reduce the size of the generated documentation.
		standardJavadocDocletOptions.addBooleanOption("-no-fonts", true)
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(JavaVersion.toVersion(buildProperties.javaVersion.version).majorVersion.toInt())
        options.isDeprecation = true
        options.compilerArgs.add("-Xlint:unchecked")
    }

	// Activate reproducible builds
	// https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives
    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}