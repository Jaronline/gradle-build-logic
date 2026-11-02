package dev.jaronline.gradle.formatting

import com.diffplug.gradle.spotless.SpotlessExtension
import dev.jaronline.gradle.common.JaronlineExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertNotNull
import kotlin.test.Test
import kotlin.test.assertEquals

class FormattingPluginTest {
	lateinit var project: Project
	lateinit var jaronline: ExtensionAware

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		project.pluginManager.apply(FormattingPlugin::class.java)
		jaronline = project.extensions.getByType<JaronlineExtension>() as ExtensionAware
	}

	@Test
	fun `plugin applies successfully`() {
		val spotless = project.extensions.findByType<SpotlessExtension>()
		val formatting = jaronline.extensions.findByName("formatting")

		assertNotNull(spotless)
		assertNotNull(formatting)
		assertInstanceOf<FormattingExtension>(formatting)
	}

	@Test
	fun `ratchetFrom applies successfully`() {
		val spotless = project.extensions.getByType<SpotlessExtension>()

		jaronline.extensions.configure<FormattingExtension> {
			ratchetFrom.set("origin/main")
		}

		project.afterEvaluate {
			assertEquals("origin/main", spotless.ratchetFrom)
		}
	}
}
