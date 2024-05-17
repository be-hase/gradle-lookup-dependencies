package dev.hsbrysk.gradle.dependencies

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class VersionCriterionTest {
    @Test
    fun `of equal`() {
        assertThat(VersionCriterion.of("1.0.0")).isEqualTo(VersionCriterion.Equal("1.0.0"))
        assertThat(VersionCriterion.of(" 1.0.0 ")).isEqualTo(VersionCriterion.Equal("1.0.0"))
    }

    @Test
    fun `of greater`() {
        assertThat(VersionCriterion.of(">1.0.0")).isEqualTo(VersionCriterion.Greater("1.0.0"))
        assertThat(VersionCriterion.of("> 1.0.0")).isEqualTo(VersionCriterion.Greater("1.0.0"))
        assertThat(VersionCriterion.of(" > 1.0.0 ")).isEqualTo(VersionCriterion.Greater("1.0.0"))
    }

    @Test
    fun `of greaterOrEqual`() {
        assertThat(VersionCriterion.of(">=1.0.0")).isEqualTo(VersionCriterion.GreaterOrEqual("1.0.0"))
        assertThat(VersionCriterion.of(">= 1.0.0")).isEqualTo(VersionCriterion.GreaterOrEqual("1.0.0"))
        assertThat(VersionCriterion.of(" >= 1.0.0 ")).isEqualTo(VersionCriterion.GreaterOrEqual("1.0.0"))
    }

    @Test
    fun `of less`() {
        assertThat(VersionCriterion.of("<1.0.0")).isEqualTo(VersionCriterion.Less("1.0.0"))
        assertThat(VersionCriterion.of("< 1.0.0")).isEqualTo(VersionCriterion.Less("1.0.0"))
        assertThat(VersionCriterion.of(" < 1.0.0 ")).isEqualTo(VersionCriterion.Less("1.0.0"))
    }

    @Test
    fun `of lessOrEqual`() {
        assertThat(VersionCriterion.of("<=1.0.0")).isEqualTo(VersionCriterion.LessOrEqual("1.0.0"))
        assertThat(VersionCriterion.of("<= 1.0.0")).isEqualTo(VersionCriterion.LessOrEqual("1.0.0"))
        assertThat(VersionCriterion.of(" <= 1.0.0 ")).isEqualTo(VersionCriterion.LessOrEqual("1.0.0"))
    }

    @Test
    fun `equal matches`() {
        val criterion = VersionCriterion.of("1.0.0")
        assertThat(criterion.matches("1.0.0")).isTrue()
        assertThat(criterion.matches("1.0.0.0")).isTrue()
        assertThat(criterion.matches("1.0.0.RELEASE")).isTrue()
        assertThat(criterion.matches("1.0.0-jvm")).isTrue()
        assertThat(criterion.matches("1.0.0.1")).isFalse()
    }

    @Test
    fun `greater matches`() {
        val criterion = VersionCriterion.of("> 1.0.0")
        assertThat(criterion.matches("1.0.1")).isTrue()
        assertThat(criterion.matches("1.0.0")).isFalse()
    }

    @Test
    fun `greaterOrEqual matches`() {
        val criterion = VersionCriterion.of(">= 1.0.0")
        assertThat(criterion.matches("1.0.1")).isTrue()
        assertThat(criterion.matches("1.0.0")).isTrue()
    }

    @Test
    fun `less matches`() {
        val criterion = VersionCriterion.of("< 1.0.0")
        assertThat(criterion.matches("0.9.9")).isTrue()
        assertThat(criterion.matches("1.0.0")).isFalse()
    }

    @Test
    fun `lessOrEqual matches`() {
        val criterion = VersionCriterion.of("<= 1.0.0")
        assertThat(criterion.matches("0.9.9")).isTrue()
        assertThat(criterion.matches("1.0.0")).isTrue()
    }
}
