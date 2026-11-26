package dev.jaronline.gradle.test.mockito

import org.gradle.api.GradleException
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.newInstance

fun Test.useMockitoAgent() {
	if (!project.pluginManager.hasPlugin("dev.jaronline.test.mockito")) {
		throw GradleException("useMockitoAgent() can only be used when the dev.jaronline.test.mockito plugin has been applied!")
	}
	val agentConfiguration = project.configurations.named("mockitoAgent")
	jvmArgumentProviders.add(
		project.objects.newInstance<MockitoAgentProvider>().apply {
			agentJar.from(agentConfiguration)
		},
	)
}
