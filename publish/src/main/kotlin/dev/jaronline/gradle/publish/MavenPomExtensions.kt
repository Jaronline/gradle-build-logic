package dev.jaronline.gradle.publish

import org.gradle.api.Action
import org.gradle.api.provider.Provider
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPomLicense
import org.gradle.api.publish.maven.MavenPomLicenseSpec

fun MavenPomLicenseSpec.mitLicense() {
	license {
		mit()
	}
}

fun MavenPomLicenseSpec.mitLicense(action: Action<MavenPomLicense>) {
	license {
		mit()
		action.execute(this)
	}
}

private fun MavenPomLicense.mit() {
	name.set("MIT License")
	url.set("https://opensource.org/license/mit/")
}

fun MavenPomDeveloperSpec.jaronlineDeveloper() {
	developer {
		id.set("jaronline")
		name.set("Jaron Zijlstra")
		url.set("https://github.com/JaronZ")
		email.set("info@jaronline.dev")
		organization.set("Jaronline")
		organizationUrl.set("https://github.com/Jaronline")
	}
}

fun MavenPom.gitScm(repository: Provider<String>) {
	scm {
		url.set(repository.map { "https://$it/" })
		connection.set(repository.map { "scm:git:git://$it.git" })
		developerConnection.set(repository.map { "scm:git:ssh://git@$it.git" })
	}
}
