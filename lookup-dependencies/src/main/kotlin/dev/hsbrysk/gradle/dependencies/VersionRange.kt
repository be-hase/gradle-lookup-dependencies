package dev.hsbrysk.gradle.dependencies

internal data class VersionRange(
    val criteria: List<VersionCriterion>,
) {
    operator fun contains(version: String): Boolean {
        return criteria.all { it.matches(version) }
    }

    companion object {
        fun of(text: String): VersionRange {
            val criteria = text
                .split(",")
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { VersionCriterion.of(it) }
            return VersionRange(criteria)
        }
    }
}
