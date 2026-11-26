package dev.jaronline.gradle.test.mockito

import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MockitoAgentProviderTest {
	lateinit var project: Project
	internal lateinit var agentProvider: MockitoAgentProvider

	@BeforeEach
	fun setup() {
		project = ProjectBuilder.builder().build()
		agentProvider = project.objects.newInstance<MockitoAgentProvider>()
	}

	@Test
	fun `test default agentJar is empty file collection`() {
		assertEquals(0, agentProvider.agentJar.count())
	}

	@Test
	fun `test asArguments returns javaAgent with agentJar path`() {
		agentProvider.agentJar.from("/path1", "/path2")
		val result = agentProvider.asArguments()
		assertEquals(listOf("-javaagent:/path1:/path2"), result)
	}
}
