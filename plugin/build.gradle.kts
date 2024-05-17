@file:Suppress("UnstableApiUsage")

plugins {
    `java-gradle-plugin`
    signing
    alias(libs.plugins.plugin.publish)
    id("conventions.kotlin")
    id("conventions.ktlint")
    id("conventions.detekt")
    id("conventions.functional-test")
}

group = "dev.hsbrysk.gradle"
version = "0.0.1"

tasks.withType<Jar> {
    archiveBaseName.set("lookup-dependencies")
}

gradlePlugin {
    val lookupDependencies by plugins.creating {
        id = "dev.hsbrysk.lookup-dependencies"
        displayName = "Lookup dependencies for Gradle"
        description = """
            You can find where a certain dependency exists in your Gradle project.
            This is useful when looking for vulnerabilities pointed out by tools like Dependabot.
        """.trimIndent()
        tags = listOf("dependencies")
        implementationClass = "dev.hsbrysk.gradle.dependencies.LookupDependenciesPlugin"
    }

    website = "https://github.com/be-hase/gradle-lookup-dependencies"
    vcsUrl = "https://github.com/be-hase/gradle-lookup-dependencies"
}

gradlePlugin.testSourceSets.add(sourceSets["functionalTest"])

signing {
    if (isOnCI) {
        useInMemoryPgpKeys(
            providers.environmentVariable("SIGNING_PGP_KEY").orNull,
            providers.environmentVariable("SIGNING_PGP_PASSWORD").orNull,
        )
    } else {
        isRequired = false
    }
}
