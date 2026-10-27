package dev.jaronline.gradle.kotlin

import dev.jaronline.gradle.common.JaronlineExtension
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class KotlinPluginTest {
	@ParameterizedTest
	@MethodSource("javaLanguageVersions")
	fun `plugin applies successfully and configures jvm toolchain`(version: JavaLanguageVersion) {
		val project: Project = ProjectBuilder.builder().build()
		project.pluginManager.apply(KotlinPlugin::class.java)

		project.extensions.configure<JaronlineExtension> {
			javaVersion.set(version)
		}

		val kotlinExtension = project.extensions.getByType<KotlinJvmProjectExtension>()
		var configuredVersion: JavaLanguageVersion? = null
		kotlinExtension.jvmToolchain {
			configuredVersion = languageVersion.orNull
		}

		assertEquals(version, configuredVersion)
	}

	companion object {
		@JvmStatic
		fun javaLanguageVersions(): List<Arguments> =
			listOf(
				Arguments.of(JavaLanguageVersion.of(17)),
				Arguments.of(JavaLanguageVersion.of(25)),
				Arguments.of(JavaLanguageVersion.of("23")),
			)
	}
}
