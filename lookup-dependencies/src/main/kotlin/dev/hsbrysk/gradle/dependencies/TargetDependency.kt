package dev.hsbrysk.gradle.dependencies

import org.gradle.api.artifacts.ModuleVersionIdentifier

internal data class TargetDependency(
    val group: String,
    val name: String,
    val version: String?,
    val versionRange: VersionRange?,
) {
    @Suppress("ReturnCount")
    fun matches(identifier: ModuleVersionIdentifier?): Boolean {
        if (identifier == null) {
            return false
        }

        // If version is specified as an identifier
        if (version != null) {
            return group == identifier.group && name == identifier.name && version == identifier.version
        }

        val matched = group == identifier.group && name == identifier.name
        // If using versionRange, do a version range search
        if (versionRange != null) {
            return matched && identifier.version in versionRange
        }
        return matched
    }

    companion object {
        fun of(
            dependency: String,
            versionRangeStr: String,
        ): TargetDependency {
            require(dependency.isNotEmpty()) { "--dependency option is required" }
            val parts = dependency.split(":")
            val versionRange = versionRangeStr.takeIf { it.isNotEmpty() }?.let { VersionRange.of(it) }
            return when (parts.size) {
                3 -> TargetDependency(parts[0], parts[1], parts[2], versionRange)
                2 -> TargetDependency(parts[0], parts[1], null, versionRange)
                else -> error("--dependency format must be <group>:<name>:<version> or <group>:<name>")
            }
        }
    }
}
