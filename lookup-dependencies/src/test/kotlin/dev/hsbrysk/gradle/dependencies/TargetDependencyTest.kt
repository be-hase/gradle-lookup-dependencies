package dev.hsbrysk.gradle.dependencies

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.mockk.mockk
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.junit.jupiter.api.Test

class TargetDependencyTest {
    @Test
    fun of() {
        assertThat(TargetDependency.of("a:b", "")).isEqualTo(TargetDependency("a", "b", null, null))
        assertThat(TargetDependency.of("a:b:1", "")).isEqualTo(TargetDependency("a", "b", "1", null))
        assertThat(TargetDependency.of("a:b", "1"))
            .isEqualTo(TargetDependency("a", "b", null, VersionRange(listOf(VersionCriterion.Equal("1")))))
    }

    @Test
    fun matches() {
        assertThat(TargetDependency.of("a:b", "").matches(SimpleIdentifier("a", "b", "1"))).isTrue()
        assertThat(TargetDependency.of("a:b:1", "").matches(SimpleIdentifier("a", "b", "1"))).isTrue()
        assertThat(TargetDependency.of("a:b:2", "").matches(SimpleIdentifier("a", "b", "1"))).isFalse()

        assertThat(TargetDependency.of("a:b", "< 3").matches(SimpleIdentifier("a", "b", "1"))).isTrue()
        assertThat(TargetDependency.of("a:b:1", "< 3").matches(SimpleIdentifier("a", "b", "2"))).isFalse()
    }

    private data class SimpleIdentifier(
        private val group: String,
        private val name: String,
        private val version: String,
    ) : ModuleVersionIdentifier {
        override fun getVersion(): String {
            return version
        }

        override fun getGroup(): String {
            return group
        }

        override fun getName(): String {
            return name
        }

        override fun getModule(): ModuleIdentifier {
            return mockk()
        }
    }
}
