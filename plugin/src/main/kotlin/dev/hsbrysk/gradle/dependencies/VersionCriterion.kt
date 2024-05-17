package dev.hsbrysk.gradle.dependencies

internal sealed interface VersionCriterion {
    val version: String

    fun matches(version: String): Boolean

    operator fun compareTo(other: String): Int {
        val versionParts = version.split(*VERSION_DELIMITERS)
        val otherParts = other.split(*VERSION_DELIMITERS)

        val length = maxOf(versionParts.size, otherParts.size)
        for (i in 0 until length) {
            val versionPart = versionParts.getOrNull(i)?.toIntOrNull() ?: 0
            val otherPart = otherParts.getOrNull(i)?.toIntOrNull() ?: 0
            if (versionPart != otherPart) {
                return versionPart.compareTo(otherPart)
            }
        }
        return 0
    }

    data class Equal(override val version: String) : VersionCriterion {
        override fun matches(version: String): Boolean {
            return this.compareTo(version) == 0
        }
    }

    data class Greater(override val version: String) : VersionCriterion {
        override fun matches(version: String): Boolean {
            return this < version
        }
    }

    data class GreaterOrEqual(override val version: String) : VersionCriterion {
        override fun matches(version: String): Boolean {
            return this <= version
        }
    }

    data class Less(override val version: String) : VersionCriterion {
        override fun matches(version: String): Boolean {
            return this > version
        }
    }

    data class LessOrEqual(override val version: String) : VersionCriterion {
        override fun matches(version: String): Boolean {
            return this >= version
        }
    }

    companion object {
        private val VERSION_DELIMITERS = arrayOf(".", "-", "_")
        private val PATTERN = """(<=?|>=?)(.+)""".toRegex()

        fun of(text: String): VersionCriterion {
            val matched = PATTERN.find(text.trim())
            val parts = if (matched == null) {
                listOf(text.trim())
            } else {
                listOf(matched.groupValues[1].trim(), matched.groupValues[2].trim())
            }
            return when (parts.size) {
                1 -> Equal(parts[0])
                2 -> when (parts[0]) {
                    ">" -> Greater(parts[1])
                    ">=" -> GreaterOrEqual(parts[1])
                    "<" -> Less(parts[1])
                    "<=" -> LessOrEqual(parts[1])
                    else -> error("invalid version expression")
                }
                else -> error("invalid version expression")
            }
        }
    }
}
