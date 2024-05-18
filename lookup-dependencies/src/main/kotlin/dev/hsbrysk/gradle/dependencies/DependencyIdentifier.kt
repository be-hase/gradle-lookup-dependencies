package dev.hsbrysk.gradle.dependencies

internal data class DependencyIdentifier(
    val group: String,
    val name: String,
    val version: String,
)
