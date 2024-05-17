package dev.hsbrysk.gradle.dependencies

import assertk.assertThat
import assertk.assertions.contains
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class LookupDependenciesPluginFunctionalTest {
    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle") }

    @Test
    fun `run - error - dependency is required`() {
        settingsFile.writeText("")
        buildFile.writeText(
            """
            plugins {
                id("dev.hsbrysk.gradle.lookup-dependencies")
            }
            """.trimIndent(),
        )

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withArguments("lookupDependencies")
            .withProjectDir(projectDir)
            .buildAndFail()

        assertThat(result.output).contains("--dependency option is required")
    }

    @Test
    fun run() {
        settingsFile.writeText("")
        buildFile.writeText(
            """
            plugins {
                id("dev.hsbrysk.gradle.lookup-dependencies")
                java
            }

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
            }
            """.trimIndent(),
        )

        GradleRunner.create()
            .withPluginClasspath()
            .withArguments("lookupDependencies", "--dependency", "org.jetbrains.kotlin:kotlin-stdlib-jdk7")
            .withProjectDir(projectDir)
            .withDebug(true)
            .build()
    }

    @Test
    fun `run with range`() {
        settingsFile.writeText("")
        buildFile.writeText(
            """
            plugins {
                id("dev.hsbrysk.gradle.lookup-dependencies")
                java
            }

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
            }
            """.trimIndent(),
        )

        GradleRunner.create()
            .withPluginClasspath()
            .withArguments(
                "lookupDependencies",
                "--dependency",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk7",
                "--version-range",
                "> 1",
            )
            .withProjectDir(projectDir)
            .withDebug(true)
            .build()
    }
}
