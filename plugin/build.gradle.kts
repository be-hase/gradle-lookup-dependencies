plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("conventions.kotlin")
    id("conventions.ktlint")
    id("conventions.detekt")
    id("conventions.functional-test")
}

group = "dev.hsbrysk.gradle"
version = "0.0.1" + if (!isOnCI) "-SNAPSHOT" else ""

tasks.withType<Jar> {
    archiveBaseName.set("lookup-dependencies")
}

gradlePlugin {
    val lookupDependencies by plugins.creating {
        id = "dev.hsbrysk.gradle.lookup-dependencies"
        implementationClass = "dev.hsbrysk.gradle.dependencies.LookupDependenciesPlugin"
    }
}

gradlePlugin.testSourceSets.add(sourceSets["functionalTest"])
