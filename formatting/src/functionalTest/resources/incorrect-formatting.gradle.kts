plugins{
	`java`
 `idea`
	`eclipse`
}

tasks.jar {
	from(sourceSets.main.get())
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

idea {
module {
		isDownloadSources = true
	isDownloadJavadoc = false
		excludeDirs.addAll(files("build", "out", "logs"))
	}
}

eclipse {
	classpath {
		isDownloadSources = true
		isDownloadJavadoc = false
	}
}