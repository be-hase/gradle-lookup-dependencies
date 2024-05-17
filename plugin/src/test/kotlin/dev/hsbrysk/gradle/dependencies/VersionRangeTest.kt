package dev.hsbrysk.gradle.dependencies

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class VersionRangeTest {
    @Test
    fun `of empty`() {
        assertThat(VersionRange.of("").criteria.isEmpty()).isTrue()
        assertThat(VersionRange.of("  ").criteria.isEmpty()).isTrue()
        assertThat(VersionRange.of(",,").criteria.isEmpty()).isTrue()
        assertThat(VersionRange.of(", , ").criteria.isEmpty()).isTrue()
    }

    @Test
    fun of() {
        assertThat(VersionRange.of("1").criteria).isEqualTo(listOf(VersionCriterion.Equal("1")))
        assertThat(VersionRange.of("1, 2").criteria).isEqualTo(
            listOf(
                VersionCriterion.Equal("1"),
                VersionCriterion.Equal("2"),
            ),
        )
    }

    @Test
    fun contains() {
        assertThat("1" in VersionRange.of("1")).isTrue()
        assertThat("1" in VersionRange.of("2")).isFalse()
        assertThat("1" in VersionRange.of("< 2")).isTrue()
        assertThat("3" in VersionRange.of("< 4, > 2")).isTrue()
        assertThat("1" in VersionRange.of("< 4, > 2")).isFalse()
        assertThat("5" in VersionRange.of("< 4, > 2")).isFalse()
    }
}
