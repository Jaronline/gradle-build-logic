package dev.jaronline.gradle.test

import dev.jaronline.gradle.BuildPropertiesExtension
import dev.jaronline.gradle.BuildPropertiesPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.the
import org.gradle.process.CommandLineArgumentProvider

class MockitoPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.configureMockito()
	}
}

private fun Project.configureMockito() {
	pluginManager.apply(BuildPropertiesPlugin::class.java)
	val buildProperties = the<BuildPropertiesExtension>()

	val mockitoAgent = configurations.create("mockitoAgent")

    dependencies.add("testImplementation", buildProperties.libraryLookup("mockito.junit.jupiter").get())
    dependencies.add(mockitoAgent.name, buildProperties.libraryLookup("mockito.core").get())?.let {
        (it as? ExternalModuleDependency)?.isTransitive = false
    }

    tasks.named<Test>("test") {
        jvmArgumentProviders.add(objects.newInstance<MockitoAgentProvider>().apply {
            agentJar.from(mockitoAgent)
        })
    }
}

private abstract class MockitoAgentProvider : CommandLineArgumentProvider {
	@get:InputFiles
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val agentJar: ConfigurableFileCollection

	override fun asArguments(): Iterable<String?> =
		listOf("-javaagent:${agentJar.asPath}")
}