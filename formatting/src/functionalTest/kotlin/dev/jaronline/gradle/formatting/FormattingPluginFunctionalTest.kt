package dev.jaronline.gradle.formatting

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.net.URL
import java.util.function.Consumer

class FormattingPluginFunctionalTest {
	@field:TempDir
	lateinit var projectDir: File

	private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }
	private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }

	@BeforeEach
	fun setup() {
		settingsFile.writeText("")
		buildFile.writeText(
			"""
			import dev.jaronline.gradle.formatting.baseJaronlineRules
			
			plugins {
				id("dev.jaronline.formatting")
			}
			
			repositories {
				gradlePluginPortal()
			}
			
			""".trimIndent(),
		)
	}

	@Test
	fun `GIVEN correctly formatted kotlinGradle file WHEN checking with default rules THEN task is successful`() {
		buildFile.appendText(
			"""
			spotless {
				kotlinGradle {
					target("test.gradle.kts")
					baseJaronlineRules()
				}
			}
			""".trimIndent(),
		)

		val testFile = projectDir.resolve("test.gradle.kts")
		testFile.writeText(getResource("correct-formatting.gradle.kts")!!.readText())

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("spotlessKotlinGradleCheck")
				.forwardOutput()
				.build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":spotlessKotlinGradleCheck")?.outcome)
	}

	@Test
	fun `GIVEN incorrectly formatted kotlinGradle file WHEN checking with default rules THEN task is failure`() {
		buildFile.appendText(
			"""
			spotless {
				kotlinGradle {
					target("test.gradle.kts")
					baseJaronlineRules()
				}
			}
			""".trimIndent(),
		)

		val testFile = projectDir.resolve("test.gradle.kts")
		testFile.writeText(getResource("incorrect-formatting.gradle.kts")!!.readText())

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("spotlessKotlinGradleCheck")
				.forwardOutput()
				.buildAndFail()

		val output = result.output.replace(" ", "")
		assertTrue(
			output.contains(
				"""
				-plugins{
				+plugins·{
				\t`java`
				-·`idea`
				+\t`idea`
				\t`eclipse`
				}
				""".trimIndent(),
			),
		)
		assertTrue(
			output.contains(
				"""
				idea·{
				-module·{
				+\tmodule·{
				\t\tisDownloadSources·=·true
				-\tisDownloadJavadoc·=·false
				+\t\tisDownloadJavadoc·=·false
				\t\texcludeDirs.addAll(files("build",·"out",·"logs"))
				\t}
				}
				""".trimIndent(),
			),
		)
		assertEquals(TaskOutcome.FAILED, result.task(":spotlessKotlinGradleCheck")?.outcome)
	}

	@DisplayName("GIVEN correct kotlin file WHEN checking with baseJaronlineRules THEN spotlessKotlinCheck task is successful")
	@ParameterizedTest(name = "GIVEN {1} WHEN checking with baseJaronlineRules THEN spotlessKotlinCheck task is successful")
	@MethodSource("correctKotlinFiles")
	fun `test correctly formatted kotlin file with base rules does not fail`(
		targetName: String,
		fileToTest: String,
	) {
		buildFile.appendText(
			"""
			spotless {
				kotlin {
					target("$targetName")
					baseJaronlineRules()
				}
			}
			""".trimIndent(),
		)

		val testFile = projectDir.resolve(targetName)
		testFile.writeText(getResource("kotlin/$fileToTest")!!.readText())

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("spotlessKotlinCheck")
				.forwardOutput()
				.build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":spotlessKotlinCheck")?.outcome)
	}

	@DisplayName("GIVEN incorrect kotlin file WHEN checking with baseJaronlineRules THEN spotlessKotlinCheck task is failure")
	@ParameterizedTest(name = "GIVEN {1} WHEN checking with baseJaronlineRules THEN spotlessKotlinCheck task is failure")
	@MethodSource("incorrectKotlinFiles")
	fun `test incorrectly formatted kotlin file with base rules fails`(
		targetName: String,
		fileToTest: String,
		assertions: Consumer<BuildResult>,
	) {
		buildFile.appendText(
			"""
			spotless {
				kotlin {
					target("$targetName")
					baseJaronlineRules()
				}
			}
			""".trimIndent(),
		)

		val testFile = projectDir.resolve(targetName)
		testFile.writeText(getResource("kotlin/$fileToTest")!!.readText())

		val result =
			GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath()
				.withArguments("spotlessKotlinCheck")
				.forwardOutput()
				.buildAndFail()

		assertEquals(TaskOutcome.FAILED, result.task(":spotlessKotlinCheck")?.outcome)
		assertions.accept(result)
	}

	private fun getResource(name: String): URL? = this::class.java.classLoader.getResource(name)

	companion object {
		@JvmStatic
		fun correctKotlinFiles(): List<Arguments> =
			listOf(
				Arguments.of("Test.kt", "correct-formatting.kt"),
				Arguments.of("SomeClass.kt", "single-class.kt"),
				Arguments.of("Test.kt", "correct-kdoc-indentation.kt"),
			)

		@JvmStatic
		fun incorrectKotlinFiles(): List<Arguments> =
			listOf(
				Arguments.of(
					"Test.kt",
					"empty-file.kt",
					Consumer<BuildResult> { result ->
						assertTrue(
							result.output.contains(
								"> The following files had format violations:\n" +
									"      Test.kt\n" +
									"          @@ -0,0 +1 @@\n" +
									"          +\n" +
									"  Run './gradlew spotlessApply' to fix all violations.",
							),
						)
					},
				),
				Arguments.of(
					"Test.kt",
					"only-a-newline.kt",
					Consumer<BuildResult> { result ->
						assertTrue(
							result.output.contains(
								"> There were 1 lint error(s), they must be fixed or suppressed.\n" +
									"  Test.kt:L1 ktlint(standard:no-empty-file) File 'Test.kt' should not be empty\n" +
									"  Resolve these lints or suppress with `suppressLintsFor`",
							),
						)
					},
				),
				Arguments.of(
					"Test.kt",
					"single-class.kt",
					Consumer<BuildResult> { result ->
						assertTrue(
							result.output.contains(
								"> There were 1 lint error(s), they must be fixed or suppressed.\n" +
									"  Test.kt:L1 ktlint(standard:filename) File 'Test.kt' contains a single class, " +
									"and possibly related top level declarations for that class. " +
									"The file should be named after the class, 'SomeClass.kt'\n" +
									"  Resolve these lints or suppress with `suppressLintsFor`",
							),
						)
					},
				),
				Arguments.of(
					"Test.kt",
					"incorrect-formatting.kt",
					Consumer<BuildResult> { result ->
						assertTrue(
							result.output.contains(
								"> The following files had format violations:\n" +
									"      Test.kt\n" +
									"          @@ -1,18 +1,15 @@\n" +
									"           class·Test·{\n" +
									"          -\\tfun·someOtherMethod():·Int\n" +
									"          -\\t\\t{\n" +
									"          +\\tfun·someOtherMethod():·Int·{\n" +
									"           \\t\\tsomeMethod(\"Some·message\")\n" +
									"           \\t\\treturn·4\n" +
									"           \\t}\n" +
									"           \n" +
									"          -\\t\\tprivate·fun·someMethod(a:·String)·{\n" +
									"          -\\t\\t·println(a)\n" +
									"          +\\tprivate·fun·someMethod(a:·String)·{\n" +
									"          +\\t\\tprintln(a)\n" +
									"           \\t\\tsomeFunction()\n" +
									"           \\t}\n" +
									"           }\n" +
									"           \n" +
									"          -··private·fun·someFunction()\n" +
									"          -{\n" +
									"          -\\tprintln(\n" +
									"          -\\t\\t\"Some·other·message\")\n" +
									"          +private·fun·someFunction()·{\n" +
									"          +\\tprintln(\"Some·other·message\")\n" +
									"           }\n" +
									"  Run './gradlew spotlessApply' to fix all violations.",
							),
						)
					},
				),
				Arguments.of(
					"Test.kt",
					"incorrect-kdoc-indentation.kt",
					Consumer<BuildResult> { result ->
						assertTrue(
							result.output.contains(
								"> The following files had format violations:\n" +
									"      Test.kt\n" +
									"          @@ -1,24 +1,24 @@\n" +
									"           /**\n" +
									"           ·*·This·class·does·some·testing\n" +
									"          -····*·@author·JaronZ\n" +
									"          +·*·@author·JaronZ\n" +
									"           ·*/\n" +
									"           class·Test·{\n" +
									"           \\t/**\n" +
									"          -\\t···*·The·current·value·of·this·test·instance\n" +
									"          +\\t·*·The·current·value·of·this·test·instance\n" +
									"           \\t·*/\n" +
									"           \\tprivate·var·currentValue:·Int\n" +
									"           \n" +
									"           \\t/**\n" +
									"           \\t·*·This·function·prints·hello·world\n" +
									"          -\\t··*/\n" +
									"          +\\t·*/\n" +
									"           \\tfun·someMethod()·{\n" +
									"           \\t\\tprintln(\"Hello·World!\")\n" +
									"           \\t}\n" +
									"           \n" +
									"           \\t/**\n" +
									"          -\\t········*·This·function·multiplies·the·current·value·by·the·given·number\n" +
									"          +\\t·*·This·function·multiplies·the·current·value·by·the·given·number\n" +
									"           \\t·*·@param·number·The·number·to·multiply·the·current·value·by\n" +
									"          -\\t······*·@return·The·resulting·number\n" +
									"          +\\t·*·@return·The·resulting·number\n" +
									"           \\t·*/\n" +
									"           \\tfun·mult(number:·Int):·Int·{\n" +
									"           \\t\\tcurrentValue·*=·number\n" +
									"          @@ -32,9 +32,9 @@\n" +
									"           \\tfun·getCurrentValue():·Int·=·currentValue\n" +
									"           \n" +
									"           \\tcompanion·object·{\n" +
									"          -\\t/**\n" +
									"          -\\t·*·A·static·method·that·prints·a·message\n" +
									"          -\\t·*/\n" +
									"          +\\t\\t/**\n" +
									"          +\\t\\t·*·A·static·method·that·prints·a·message\n" +
									"          +\\t\\t·*/\n" +
									"           \\t\\t@JvmStatic\n" +
									"           \\t\\tfun·someStaticMethod()·{\n" +
									"           \\t\\t\\tprintln(\"This·is·a·static·method!\")\n" +
									"          @@ -42,18 +42,18 @@\n" +
									"           \\t}\n" +
									"           }\n" +
									"           \n" +
									"          -\\t/**\n" +
									"          -·····*·This·function·prints·a·test·message\n" +
									"      ... (18 more lines that didn't fit)\n" +
									"  Run './gradlew spotlessApply' to fix all violations.",
							),
						)
					},
				),
			)
	}
}
